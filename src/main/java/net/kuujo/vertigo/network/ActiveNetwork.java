/*
 * Copyright 2014 the original author or authors.
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
package net.kuujo.vertigo.network;

import net.kuujo.vertigo.input.grouping.MessageGrouping;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

/**
 * Active network.
 *
 * @author Jordan Halterman
 */
public interface ActiveNetwork extends ActiveConfig<NetworkConfig> {

  /**
   * Adds a component to the network.
   *
   * @param component The component to add.
   * @return The added component.
   */
  <T extends ComponentConfig<T>> T addComponent(T component);

  /**
   * Adds a component to the network.
   *
   * @param component The component to add.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The added component.
   */
  <T extends ComponentConfig<T>> T addComponent(T component, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleOrMain The component module name or verticle main.
   * @return The component configuration.
   */
  <T extends ComponentConfig<T>> T addComponent(String name, String moduleOrMain);

  /**
   * Adds a component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleOrMain The component module name or verticle main.
   * @param config The component configuration.
   * @return The component configuration.
   */
  <T extends ComponentConfig<T>> T addComponent(String name, String moduleOrMain, JsonObject config);

  /**
   * Adds a component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleOrMain The component module name or verticle main.
   * @param instances The number of instances of the component.
   * @return The component configuration.
   */
  <T extends ComponentConfig<T>> T addComponent(String name, String moduleOrMain, int instances);

  /**
   * Adds a component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleOrMain The component module name or verticle main.
   * @param config The component configuration.
   * @param instances The number of instances of the component.
   * @return The component configuration.
   */
  <T extends ComponentConfig<T>> T addComponent(String name, String moduleOrMain, JsonObject config, int instances);

  /**
   * Adds a component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleOrMain The component module name or verticle main.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The component configuration.
   */
  <T extends ComponentConfig<T>> T addComponent(String name, String moduleOrMain, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleOrMain The component module name or verticle main.
   * @param config The component configuration.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The component configuration.
   */
  <T extends ComponentConfig<T>> T addComponent(String name, String moduleOrMain, JsonObject config, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleOrMain The component module name or verticle main.
   * @param instances The number of instances of the component.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The component configuration.
   */
  <T extends ComponentConfig<T>> T addComponent(String name, String moduleOrMain, int instances, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleOrMain The component module name or verticle main.
   * @param config The component configuration.
   * @param instances The number of instances of the component.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The component configuration.
   */
  <T extends ComponentConfig<T>> T addComponent(String name, String moduleOrMain, JsonObject config, int instances, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Removes a component from the network.
   *
   * @param component The component to remove.
   * @return The removed component configuration.
   */
  <T extends ComponentConfig<T>> T removeComponent(T component);

  /**
   * Removes a component from the network.
   *
   * @param component The component to remove.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The removed component configuration.
   */
  <T extends ComponentConfig<T>> T removeComponent(T component, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Removes a component from the network.
   *
   * @param name The name of the component to remove.
   * @return The removed component configuration.
   */
  <T extends ComponentConfig<T>> T removeComponent(String name);

  /**
   * Removes a component from the network.
   *
   * @param name The name of the component to remove.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The removed component configuration.
   */
  <T extends ComponentConfig<T>> T removeComponent(String name, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a module component to the network.
   *
   * @param module The module component configuration.
   * @return The added module configuration.
   */
  ModuleConfig addModule(ModuleConfig module);

  /**
   * Adds a module component to the network.
   *
   * @param module The module component configuration.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The added module configuration.
   */
  ModuleConfig addModule(ModuleConfig module, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a module component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleName The component module name.
   * @return The added module configuration.
   */
  ModuleConfig addModule(String name, String moduleName);

  /**
   * Adds a module component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleName The component module name.
   * @param config The module component configuration.
   * @return The added module configuration.
   */
  ModuleConfig addModule(String name, String moduleName, JsonObject config);

  /**
   * Adds a module component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleName The component module name.
   * @param instances The number of instances to be deployed.
   * @return The added module configuration.
   */
  ModuleConfig addModule(String name, String moduleName, int instances);

  /**
   * Adds a module component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleName The component module name.
   * @param config The module component configuration.
   * @param instances The number of instances to be deployed.
   * @return The added module configuration.
   */
  ModuleConfig addModule(String name, String moduleName, JsonObject config, int instances);

  /**
   * Adds a module component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleName The component module name.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The added module configuration.
   */
  ModuleConfig addModule(String name, String moduleName, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a module component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleName The component module name.
   * @param config The module component configuration.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The added module configuration.
   */
  ModuleConfig addModule(String name, String moduleName, JsonObject config, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a module component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleName The component module name.
   * @param instances The number of instances to be deployed.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The added module configuration.
   */
  ModuleConfig addModule(String name, String moduleName, int instances, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a module component to the network.
   *
   * @param name The name of the component to add.
   * @param moduleName The component module name.
   * @param config The module component configuration.
   * @param instances The number of instances to be deployed.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The added module configuration.
   */
  ModuleConfig addModule(String name, String moduleName, JsonObject config, int instances, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Removes a module component from the network.
   *
   * @param module The module configuration to remove.
   * @return The removed module configuration.
   */
  ModuleConfig removeModule(ModuleConfig module);

  /**
   * Removes a module component from the network.
   *
   * @param module The module configuration to remove.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The removed module configuration.
   */
  ModuleConfig removeModule(ModuleConfig module, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Removes a module component from the network.
   *
   * @param name The name of the module component to remove.
   * @return The removed module configuration.
   */
  ModuleConfig removeModule(String name);

  /**
   * Removes a module component from the network.
   *
   * @param name The name of the module component to remove.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The removed module configuration.
   */
  ModuleConfig removeModule(String name, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a verticle component to the network.
   *
   * @param verticle The verticle component configuration.
   * @return The added verticle configuration.
   */
  VerticleConfig addVerticle(VerticleConfig verticle);

  /**
   * Adds a verticle component to the network.
   *
   * @param verticle The verticle component configuration.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The added verticle configuration.
   */
  VerticleConfig addVerticle(VerticleConfig verticle, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a verticle component to the network.
   *
   * @param name The name of the component to add.
   * @param main The component verticle main.
   * @return The added verticle configuration.
   */
  VerticleConfig addVerticle(String name, String main);

  /**
   * Adds a verticle component to the network.
   *
   * @param name The name of the component to add.
   * @param main The component verticle main.
   * @param config The verticle component configuration.
   * @return The added verticle configuration.
   */
  VerticleConfig addVerticle(String name, String main, JsonObject config);

  /**
   * Adds a verticle component to the network.
   *
   * @param name The name of the component to add.
   * @param main The component verticle main.
   * @param instances The number of instances to be deployed.
   * @return The added verticle configuration.
   */
  VerticleConfig addVerticle(String name, String main, int instances);

  /**
   * Adds a verticle component to the network.
   *
   * @param name The name of the component to add.
   * @param main The component verticle main.
   * @param config The verticle component configuration.
   * @param instances The number of instances to be deployed.
   * @return The added verticle configuration.
   */
  VerticleConfig addVerticle(String name, String main, JsonObject config, int instances);

  /**
   * Adds a verticle component to the network.
   *
   * @param name The name of the component to add.
   * @param main The component verticle main.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The added verticle configuration.
   */
  VerticleConfig addVerticle(String name, String main, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a verticle component to the network.
   *
   * @param name The name of the component to add.
   * @param main The component verticle main.
   * @param config The verticle component configuration.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The added verticle configuration.
   */
  VerticleConfig addVerticle(String name, String main, JsonObject config, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a verticle component to the network.
   *
   * @param name The name of the component to add.
   * @param main The component verticle main.
   * @param instances The number of instances to be deployed.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The added verticle configuration.
   */
  VerticleConfig addVerticle(String name, String main, int instances, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Adds a verticle component to the network.
   *
   * @param name The name of the component to add.
   * @param main The component verticle main.
   * @param config The verticle component configuration.
   * @param instances The number of instances to be deployed.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The added verticle configuration.
   */
  VerticleConfig addVerticle(String name, String main, JsonObject config, int instances, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Removes a verticle component from the network.
   *
   * @param verticle The verticle configuration to remove.
   * @return The removed verticle configuration.
   */
  VerticleConfig removeVerticle(VerticleConfig verticle);

  /**
   * Removes a verticle component from the network.
   *
   * @param verticle The verticle configuration to remove.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The removed verticle configuration.
   */
  VerticleConfig removeVerticle(VerticleConfig verticle, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Removes a verticle component from the network.
   *
   * @param name The name of the verticle component to remove.
   * @return The removed verticle configuration.
   */
  VerticleConfig removeVerticle(String name);

  /**
   * Removes a verticle component from the network.
   *
   * @param name The name of the verticle component to remove.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The removed verticle configuration.
   */
  VerticleConfig removeVerticle(String name, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Creates a connection between two components in the network.
   *
   * @param connection The connection configuration.
   * @return The added connection configuration.
   */
  ConnectionConfig createConnection(ConnectionConfig connection);

  /**
   * Creates a connection between two components in the network.
   *
   * @param connection The connection configuration.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The added connection configuration.
   */
  ConnectionConfig createConnection(ConnectionConfig connection, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Creates a connection between two components in the network.
   *
   * @param source The connection source and port.
   * @param target The connection target and port.
   * @return The new connection configuration.
   */
  ConnectionConfig createConnection(String source, String target);

  /**
   * Creates a connection between two components in the network.
   *
   * @param source The connection source and port.
   * @param target The connection target and port.
   * @param grouping The connection grouping.
   * @return The new connection configuration.
   */
  ConnectionConfig createConnection(String source, String target, MessageGrouping grouping);

  /**
   * Creates a connection between two components in the network.
   *
   * @param source The connection source.
   * @param out The connection source out port.
   * @param target The connection target.
   * @param in The connection target in port.
   * @return The new connection configuration.
   */
  ConnectionConfig createConnection(String source, String out, String target, String in);

  /**
   * Creates a connection between two components in the network.
   *
   * @param source The connection source.
   * @param out The connection source out port.
   * @param target The connection target.
   * @param in The connection target in port.
   * @param grouping The connection grouping.
   * @return The new connection configuration.
   */
  ConnectionConfig createConnection(String source, String out, String target, String in, MessageGrouping grouping);

  /**
   * Creates a connection between two components in the network.
   *
   * @param source The connection source and port.
   * @param target The connection target and port.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The new connection configuration.
   */
  ConnectionConfig createConnection(String source, String target, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Creates a connection between two components in the network.
   *
   * @param source The connection source and port.
   * @param target The connection target and port.
   * @param grouping The connection grouping.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The new connection configuration.
   */
  ConnectionConfig createConnection(String source, String target, MessageGrouping grouping, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Creates a connection between two components in the network.
   *
   * @param source The connection source.
   * @param out The connection source out port.
   * @param target The connection target.
   * @param in The connection target in port.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The new connection configuration.
   */
  ConnectionConfig createConnection(String source, String out, String target, String in, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Creates a connection between two components in the network.
   *
   * @param source The connection source.
   * @param out The connection source out port.
   * @param target The connection target.
   * @param in The connection target in port.
   * @param grouping The connection grouping.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The new connection configuration.
   */
  ConnectionConfig createConnection(String source, String out, String target, String in, MessageGrouping grouping, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Destroys a connection between two components in the network.
   *
   * @param connection The connection configuration to destroy.
   * @return The active network.
   */
  ActiveNetwork destroyConnection(ConnectionConfig connection);

  /**
   * Destroys a connection between two components in the network.
   *
   * @param source The connection source and port.
   * @param target The connection target and port.
   * @return The active network.
   */
  ActiveNetwork destroyConnection(String source, String target);

  /**
   * Destroys a connection between two components in the network.
   *
   * @param source The connection source.
   * @param out The connection source out port.
   * @param target The connection target.
   * @param in The connection target in port.
   * @return The active network.
   */
  ActiveNetwork destroyConnection(String source, String out, String target, String in);

  /**
   * Destroys a connection between two components in the network.
   *
   * @param connection The connection configuration to destroy.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The active network.
   */
  ActiveNetwork destroyConnection(ConnectionConfig connection, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Destroys a connection between two components in the network.
   *
   * @param source The connection source and port.
   * @param target The connection target and port.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The active network.
   */
  ActiveNetwork destroyConnection(String source, String target, Handler<AsyncResult<ActiveNetwork>> doneHandler);

  /**
   * Destroys a connection between two components in the network.
   *
   * @param source The connection source.
   * @param out The connection source out port.
   * @param target The connection target.
   * @param in The connection target in port.
   * @param doneHandler An asynchronous handler to be called once complete.
   * @return The active network.
   */
  ActiveNetwork destroyConnection(String source, String out, String target, String in, Handler<AsyncResult<ActiveNetwork>> doneHandler);

}
