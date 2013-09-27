/*
* Copyright 2013 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package net.kuujo.vevent.network;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.kuujo.vevent.context.ComponentContext;
import net.kuujo.vevent.context.NetworkContext;
import net.kuujo.vevent.context.WorkerContext;
import net.kuujo.vevent.definition.ComponentDefinition;
import net.kuujo.via.cluster.Cluster;
import net.kuujo.via.heartbeat.DefaultHeartbeatMonitor;
import net.kuujo.via.heartbeat.HeartbeatMonitor;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.impl.DefaultFutureResult;
import org.vertx.java.core.json.JsonObject;

/**
 * An abstract network coordinator.
 *
 * @author Jordan Halterman
 */
abstract class AbstractCoordinator extends BusModBase implements Handler<Message<JsonObject>> {

  protected NetworkContext context;

  protected Cluster cluster;

  protected Set<String> ready = new HashSet<>();

  protected Map<String, String> deploymentMap = new HashMap<>();

  protected Map<String, WorkerContext> contextMap = new HashMap<>();

  protected Map<String, HeartbeatMonitor> heartbeats = new HashMap<>();

  private boolean allDeployed;

  @Override
  public void start() {
    super.start();
    context = new NetworkContext(config);
    eb.registerHandler(context.getAddress(), this);
    doDeploy();
  }

  @Override
  public void handle(Message<JsonObject> message) {
    String action = getMandatoryString("action", message);
    switch (action) {
      case "ready":
        doReady(message);
        break;
      case "register":
        doRegister(message);
        break;
      case "unregister":
        doUnregister(message);
        break;
      case "shutdown":
        doShutdown(message);
        break;
      case "redeploy":
        doRedeployAll(message);
        break;
      default:
        sendError(message, "Invalid action.");
        break;
    }
  }

  /**
   * Deploys the network.
   */
  private void doDeploy() {
    new RecursiveDeployer(context).deploy(new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> result) {
        if (result.failed()) {
          container.logger().error("Failed to deploy network.");
          container.exit();
        }
        else {
          allDeployed = true;
        }
      }
    });
  }

  /**
   * Indicates that a worker is ready.
   */
  private void doReady(Message<JsonObject> message) {
    String address = getMandatoryString("address", message);
    ready.add(address);
    if (allDeployed && ready.size() == contextMap.size()) {
      container.deployVerticle(Authenticator.class.getName(), new JsonObject().putString("address", context.getAuthenticatorAddress()).putString("broadcast", context.getBroadcastAddress()), new Handler<AsyncResult<String>>() {
        @Override
        public void handle(AsyncResult<String> result) {
          if (result.failed()) {
            logger.error("Failed to deploy authenticator verticle.");
          }
          else {
            for (ComponentContext componentContext : context.getComponentContexts()) {
              for (WorkerContext workerContext : componentContext.getWorkerContexts()) {
                doStart(workerContext);
              }
            }
          }
        }
      });
    }
  }

  /**
   * Starts a worker context.
   */
  private void doStart(WorkerContext workerContext) {
    doStart(workerContext, 0);
  }

  /**
   * Starts a worker context.
   */
  private void doStart(final WorkerContext workerContext, final int count) {
    eb.sendWithTimeout(workerContext.getAddress(), new JsonObject().putString("action", "start"), 10000, new Handler<AsyncResult<Message<Void>>>() {
      @Override
      public void handle(AsyncResult<Message<Void>> result) {
        if (result.failed()) {
          if (count < 3) {
            doStart(workerContext, count+1);
          }
          else {
            logger.error("Failed to start worker at " + workerContext.getAddress() + ".");
          }
        }
      }
    });
  }

  /**
   * Creates a unique heartbeat address.
   */
  private String createHeartbeatAddress() {
    return UUID.randomUUID().toString();
  }

  /**
   * Registers a heartbeat.
   */
  private void doRegister(Message<JsonObject> message) {
    final String address = getMandatoryString("address", message);
    String heartbeatAddress = createHeartbeatAddress();
    HeartbeatMonitor monitor = new DefaultHeartbeatMonitor(heartbeatAddress, vertx);
    monitor.listen(new Handler<String>() {
      @Override
      public void handle(String heartbeatAddress) {
        if (heartbeats.containsKey(address)) {
          heartbeats.remove(address);
          doRedeploy(address);
        }
      }
    });
    heartbeats.put(address, monitor);
    message.reply(heartbeatAddress);
  }

  /**
   * Unregisters a heartbeat.
   */
  private void doUnregister(Message<JsonObject> message) {
    final String address = getMandatoryString("address", message);
    if (heartbeats.containsKey(address)) {
      HeartbeatMonitor monitor = heartbeats.get(address);
      monitor.unlisten();
      heartbeats.remove(address);
    }
    doRedeploy(address);
  }

  /**
   * Redeploys a worker.
   */
  private void doRedeploy(final String address) {
    if (deploymentMap.containsKey(address)) {
      String deploymentID = deploymentMap.get(address);
      cluster.undeployVerticle(deploymentID, new Handler<AsyncResult<Void>>() {
        @Override
        public void handle(AsyncResult<Void> result) {
          deploymentMap.remove(address);
          if (contextMap.containsKey(address)) {
            final WorkerContext context = contextMap.get(address);
            cluster.deployVerticle(context.getContext().getDefinition().getMain(), context.serialize(), new Handler<AsyncResult<String>>() {
              @Override
              public void handle(AsyncResult<String> result) {
                if (result.succeeded()) {
                  deploymentMap.put(context.getAddress(), result.result());
                }
                else {
                  container.logger().error("Failed to redeploy worker at " + address + ".");
                }
              }
            });
          }
        }
      });
    }
  }

  /**
   * Shuts down the network.
   */
  private void doShutdown(final Message<JsonObject> message) {
    new RecursiveDeployer(context).undeploy(new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> result) {
        message.reply(result.succeeded());
      }
    });
  }

  /**
   * Redeploys the entire network.
   */
  private void doRedeployAll(final Message<JsonObject> message) {
    new RecursiveDeployer(context).undeploy(new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> result) {
        new RecursiveDeployer(context).deploy(new Handler<AsyncResult<Void>>() {
          @Override
          public void handle(AsyncResult<Void> result) {
            message.reply(result.succeeded());
          }
        });
      }
    });
  }

  /**
   * Recursively deploys all network components.
   *
   * @author Jordan Halterman
   */
  private class RecursiveDeployer {

    private NetworkContext context;

    public RecursiveDeployer(NetworkContext context) {
      this.context = context;
    }

    /**
     * Deploys the network.
     *
     * @param doneHandler
     *   A handler to be invoked once the network is deployed.
     */
    public void deploy(Handler<AsyncResult<Void>> doneHandler) {
      Collection<ComponentContext> components = context.getComponentContexts();
      RecursiveComponentDeployer deployer = new RecursiveComponentDeployer(components);
      deployer.deploy(doneHandler);
    }

    /**
     * Undeploys the network.
     *
     * @param doneHandler
     *   A handler to be invoked once the network is undeployed.
     */
    public void undeploy(Handler<AsyncResult<Void>> doneHandler) {
      Collection<ComponentContext> components = context.getComponentContexts();
      RecursiveComponentDeployer deployer = new RecursiveComponentDeployer(components);
      deployer.undeploy(doneHandler);
    }

    /**
     * An abstract context deployer.
     *
     * @param <T> The context type.
     */
    private abstract class RecursiveContextDeployer<T> {
      protected Iterator<T> iterator;
      protected Future<Void> future;

      protected Handler<AsyncResult<String>> assignHandler = new Handler<AsyncResult<String>>() {
        @Override
        public void handle(AsyncResult<String> result) {
          if (result.succeeded()) {
            if (iterator.hasNext()) {
              doDeploy(iterator.next(), assignHandler);
            }
            else {
              future.setResult(null);
            }
          }
          else {
            future.setFailure(result.cause());
          }
        }
      };

      protected Handler<AsyncResult<Void>> releaseHandler = new Handler<AsyncResult<Void>>() {
        @Override
        public void handle(AsyncResult<Void> result) {
          if (result.succeeded()) {
            if (iterator.hasNext()) {
              doUndeploy(iterator.next(), releaseHandler);
            }
            else {
              future.setResult(null);
            }
          }
          else {
            future.setFailure(result.cause());
          }
        }
      };

      public RecursiveContextDeployer(Collection<T> contexts) {
        this.iterator = contexts.iterator();
      }

      /**
       * Deploys a network.
       *
       * @param doneHandler
       *   The handler to invoke once deployment is complete.
       */
      public void deploy(Handler<AsyncResult<Void>> doneHandler) {
        this.future = new DefaultFutureResult<Void>();
        future.setHandler(doneHandler);
        if (iterator.hasNext()) {
          doDeploy(iterator.next(), assignHandler);
        }
        else {
          future.setResult(null);
        }
      }

      /**
       * Undeploys a network.
       *
       * @param doneHandler
       *   The handler to invoke once deployment is complete.
       */
      public void undeploy(Handler<AsyncResult<Void>> doneHandler) {
        this.future = new DefaultFutureResult<Void>();
        future.setHandler(doneHandler);
        if (iterator.hasNext()) {
          doUndeploy(iterator.next(), releaseHandler);
        }
        else {
          future.setResult(null);
        }
      }

      /**
       * Deploys a context.
       */
      protected abstract void doDeploy(T context, Handler<AsyncResult<String>> doneHandler);

      /**
       * Undeploys a context.
       */
      protected abstract void doUndeploy(T context, Handler<AsyncResult<Void>> doneHandler);

    }

    /**
     * A network component deployer.
     */
    private class RecursiveComponentDeployer extends RecursiveContextDeployer<ComponentContext> {

      public RecursiveComponentDeployer(Collection<ComponentContext> contexts) {
        super(contexts);
      }

      @Override
      protected void doDeploy(ComponentContext context, Handler<AsyncResult<String>> resultHandler) {
        final Future<String> future = new DefaultFutureResult<String>();
        future.setHandler(resultHandler);
        Collection<WorkerContext> workers = context.getWorkerContexts();
        RecursiveWorkerDeployer deployer = new RecursiveWorkerDeployer(workers);
        deployer.deploy(new Handler<AsyncResult<Void>>() {
          @Override
          public void handle(AsyncResult<Void> result) {
            if (result.succeeded()) {
              future.setResult("");
            }
            else {
              future.setFailure(result.cause());
            }
          }
        });
      }

      @Override
      protected void doUndeploy(ComponentContext context, Handler<AsyncResult<Void>> doneHandler) {
        final Future<Void> future = new DefaultFutureResult<Void>();
        future.setHandler(doneHandler);
        Collection<WorkerContext> workers = context.getWorkerContexts();
        RecursiveWorkerDeployer executor = new RecursiveWorkerDeployer(workers);
        executor.undeploy(new Handler<AsyncResult<Void>>() {
          @Override
          public void handle(AsyncResult<Void> result) {
            if (result.succeeded()) {
              future.setResult(null);
            }
            else {
              future.setFailure(result.cause());
            }
          }
        });
      }
    }

    /**
     * A network worker deployer.
     */
    private class RecursiveWorkerDeployer extends RecursiveContextDeployer<WorkerContext> {

      public RecursiveWorkerDeployer(Collection<WorkerContext> contexts) {
        super(contexts);
      }

      @Override
      protected void doDeploy(final WorkerContext context, Handler<AsyncResult<String>> resultHandler) {
        final Future<String> future = new DefaultFutureResult<String>();
        contextMap.put(context.getAddress(), context);
        future.setHandler(resultHandler);

        ComponentDefinition definition = context.getContext().getDefinition();
        cluster.deployVerticle(definition.getMain(), context.serialize(), new Handler<AsyncResult<String>>() {
          @Override
          public void handle(AsyncResult<String> result) {
            if (result.succeeded()) {
              deploymentMap.put(context.getAddress(), result.result());
              future.setResult(result.result());
            }
            else {
              future.setFailure(result.cause());
            }
          }
        });
      }

      @Override
      protected void doUndeploy(WorkerContext context, Handler<AsyncResult<Void>> resultHandler) {
        final Future<Void> future = new DefaultFutureResult<Void>();
        future.setHandler(resultHandler);
        String address = context.getAddress();
        if (deploymentMap.containsKey(address)) {
          String deploymentID = deploymentMap.get(address);
          cluster.undeployVerticle(deploymentID, new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> result) {
              if (result.failed()) {
                future.setFailure(result.cause());
              }
              else {
                future.setResult(result.result());
              }
            }
          });
        }
      }
    }

  }

}
