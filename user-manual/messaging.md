---
layout: content
menu: user-manual
title: Messaging
---

# Messaging

* [Sending messages on an output port](#sending-messages-on-an-output-port)
* [Receiving messages on an input port](#receiving-messages-on-an-input-port)
* [Working with message groups](#working-with-message-groups)
* [Working with message batches](#working-with-message-batches)
* [Providing serializeable messages](#providing-serializeable-messages)

The Vertigo messaging API is simply a wrapper around the Vert.x event bus.
Vertigo messages are not sent through any central router. Rather, Vertigo uses
network configurations to create direct event bus connections between components.
Vertigo components send and receive messages using only output and input *ports*
and are hidden from event bus address details which are defined in network configurations.
This is the element that makes Vertigo components reusable.

Rather than routing messages through a central router, components communicate
directly with one another over the event bus, ensuring optimal performance.

![Direct connections](http://s21.postimg.org/65oa1e3dj/Untitled_Diagram_1.png)

Vertigo messages are guaranteed to arrive *in the order in which they were sent*
and to only be processed *exactly once*. Vertigo also provides an API
that allows for logical grouping and ordering of collections of messages known as
[groups](#working-with-message-groups). Groups are strongly ordered named batches
of messages that can be nested.

For more information on messaging see [how Vertigo handles messaging](#how-vertigo-handles-messaging)

### Sending messages on an output port
To reference an output port, use the `output.port(String name)` method.

{% include snippet.html ex="1" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex1-java">

{:.prettyprint .lang-java}
	OutputPort port = output.port("out");
	
</div>
<div class="tab-pane" id="ex1-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex1-javascript">
  
TODO
	
</div>
</div>

If the referenced output port is not defined in the network configuration, the
port will be lazily created, though it will not actually reference any connections.

Any message that can be sent on the Vert.x event bus can be sent on the output port.
To send a message on the event bus, simply call the `send` method.

{% include snippet.html ex="2" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex2-java">

{:.prettyprint .lang-java}
	output.port("out").send("Hello world!");
	
</div>
<div class="tab-pane" id="ex2-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex2-javascript">
  
TODO
	
</div>
</div>

Internally, Vertigo will route the message to any connections as defined in the
network configuration.

Output ports also support custom message serialization.
See [providing serializeable messages](#providing-serializeable-messages)

### Receiving messages on an input port
Input ports are referenced in the same was as output ports.

{% include snippet.html ex="3" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex3-java">

{:.prettyprint .lang-java}	
	InputPort port = input.port("in");
	
</div>
<div class="tab-pane" id="ex3-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex3-javascript">
  
TODO
	
</div>
</div>

To receive messages on an input port, register a message handler on the port.

{% include snippet8.html ex="4" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex4-java">

{:.prettyprint .lang-java}
	input.port("in").messageHandler(new Handler<String>() {
	  public void handle(String message) {
	    output.port("out").send(message);
	  }
	});
	
</div>
<div class="tab-pane active" id="ex4-java8">

{:.prettyprint .lang-java}
	input.port("in").messageHandler((message) -> output.port("out").send(message));
	
</div>
<div class="tab-pane" id="ex4-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex4-javascript">
  
TODO
	
</div>
</div>

Note that Vertigo messages arrive in plain format and not in any sort of `Message`
wrapper. This is because Vertigo messages are inherently uni-directional, and message
acking is handled internally.

### Working with message groups
Vertigo provides a mechanism for logically grouping messages appropriately
named *groups*. Groups are named logical collections of messages that are strongly
ordered by name. Before any given group can stat, each of the groups of the same
name at the same level that preceded it must have been completed. Additionally,
messages within a group are *guaranteed to be delivered to the same instance* of each
target component. In other words, routing is performed per-group rather than per-message.

![Groups](http://s30.postimg.org/655svvk3l/groups.png)

When a new output group is created, Vertigo will await the completion of all groups
of the same name that were created prior to the new group before sending the new group's
messages.

{% include snippet8.html ex="5" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex5-java">

{:.prettyprint .lang-java}
	output.port("out").group("foo", new Handler<OutputGroup>() {
	  public void handle(OutputGroup group) {
	    group.send("foo").send("bar").send("baz").end();
	  }
	});
	
</div>
<div class="tab-pane active" id="ex5-java8">

{:.prettyprint .lang-java}
	output.port("out").group("foo", (group) -> group.send("foo").send("bar").send("baz").end());
	
</div>
<div class="tab-pane" id="ex5-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex5-javascript">
  
TODO
	
</div>
</div>

Note that the group's `end()` method *must* be called in order to indicate completion of
the group. *Groups are fully asynchronous*, meaning they support asynchronous calls to other
APIs, and this step is crucial to that functionality.

{% include snippet8.html ex="6" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex6-java">

{:.prettyprint .lang-java}
	output.port("out").group("foo", new Handler<OutputGroup>() {
	  public void handle(final OutputGroup group) {
	    someObject.someAsyncApi(new Handler<AsyncResult<String>>() {
	      public void handle(AsyncResult<String> result) {
	        if (result.succeeded()) {
	          group.send(result.result()).end();
	        }
	      }
	    });
	  }
	});
	
</div>
<div class="tab-pane active" id="ex6-java8">

{:.prettyprint .lang-java}
	output.port("out").group("foo", (group) -> {
	    someObject.someAsyncApi((result) -> {
	    if (result.succeeded()) {
	      group.send(result.result()).end();
	    }
	  });
	});
	
</div>
<div class="tab-pane" id="ex6-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex6-javascript">
  
TODO
	
</div>
</div>

The `OutputGroup` API exposes the same methods as the `OutputPort`. That means that groups
can be nested and Vertigo will still guarantee ordering across groups.

{% include snippet8.html ex="7" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex7-java">

{:.prettyprint .lang-java}
	output.port("out").group("foo", new Handler<OutputGroup>() {
	  public void handle(OutputGroup group) {
	    group.group("bar", new Handler<OutputGroup>() {
	      public void handle(OutputGroup group) {
	        group.send(1).send(2).send(3).end();
	      }
	    });
	    group.group("baz", new Handler<OutputGroup>() {
	      public void handle(OutputGroup group) {
	        group.send(4).send(5).send(6).end();
	      }
	    });
	    // Since two child groups were created, this group will not be ended
	    // until both children have been ended.
	    group.end();
	  }
	});
	
</div>
<div class="tab-pane active" id="ex7-java8">

{:.prettyprint .lang-java}
	output.port("out").group("foo", (fooGroup) -> {
	    fooGroup.group("bar", (group) -> group.send(1).send(2).send(3).end());
	    fooGroup.group("baz", (group) -> group.send(4).send(5).send(6).end());
	    // Since two child groups were created, this group will not be ended
	    // until both children have been ended.
	    fooGroup.end();
	});
	
</div>
<div class="tab-pane" id="ex7-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex7-javascript">
  
TODO
	
</div>
</div>

As with receiving messages, to receive message groups register a handler on an
input port using the `groupHandler` method, passing a group name as the first
argument.

{% include snippet8.html ex="8" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex8-java">

{:.prettyprint .lang-java}
	input.port("in").groupHandler("foo", new Handler<InputGroup>() {
	  public void handle(InputGroup group) {
	    group.messageHandler(new Handler<String>() {
	      public void handle(String message) {
	        output.port("out").send(message);
	      }
	    });
	  }
	});
	
</div>
<div class="tab-pane active" id="ex8-java8">

{:.prettyprint .lang-java}
	input.port("in").groupHandler("foo", (group) -> group.messageHandler((message) -> output.port("out").send(message)));
	
</div>
<div class="tab-pane" id="ex8-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex8-javascript">
  
TODO
	
</div>
</div>

The `InputGroup` API also supports a `startHandler` and `endHandler`. The `endHandler`
can be particularly useful for aggregations. Vertigo guarantees that if a group's
`endHandler` is called then *all* of the messages sent for that group were received
by that group.

{% include snippet8.html ex="9" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex9-java">

{:.prettyprint .lang-java}
	input.port("in").groupHandler("foo", new Handler<InputGroup>() {
	  public void handle(InputGroup group) {
	    final Set<String> messages = new HashSet<>();
	
	    group.messageHandler(new Handler<String>() {
	      public void handle(String message) {
	        messages.add(message);
	      }
	    });
	
	    group.endHandler(new Handler<Void>() {
	      public void handle(Void ignore) {
	        System.out.println("Received " + messages.size() + " messages in group.");
	      }
	    });
	  }
	});
	
</div>
<div class="tab-pane active" id="ex9-java8">

{:.prettyprint .lang-java}
	input.port("in").groupHandler("foo", (group) -> {
	  final Set<String> messages = new HashSet<>();
	  group.messageHandler((message) -> messages.add(message));
	  group.endHandler((m) -> System.out.println("Received " + messages.size() + " messages in group."));
	});
	
</div>
<div class="tab-pane" id="ex9-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex9-javascript">
  
TODO
	
</div>
</div>

As with output groups, input groups can be nested, representing the same structure
sent by an output group.

{% include snippet8.html ex="10" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex10-java">

{:.prettyprint .lang-java}
	input.port("in").groupHandler("foo", new Handler<InputGroup>() {
	  public void handle(InputGroup group) {
	    group.group("bar", new Handler<InputGroup>() {
	      public void handle(InputGroup group) {
	        group.messageHandler(new Handler<Integer>() {
	          public void handle(Integer number) {
	            output.port("bar").send(number);
	          }
	        });
	      }
	    });
	    group.group("baz", new Handler<InputGroup>() {
	      public void handle(InputGroup group) {
	        group.messageHandler(new Handler<String>() {
	          public void handle(String string) {
	            output.port("baz").send(string);
	          }
	        });
	      }
	    });
	  }
	});
	
</div>
<div class="tab-pane active" id="ex10-java8">

{:.prettyprint .lang-java}
	input.port("in").groupHandler("foo", (fooGroup) -> {
	  fooGroup.group("bar", (barGroup) -> barGroup.messageHandler((number) -> output.port("bar").send(number)));
	  fooGroup.group("baz", (bazGroup) -> group.messageHandler((string) -> output.port("baz").send(string)));
	});
	
</div>
<div class="tab-pane" id="ex10-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex10-javascript">
  
TODO
	
</div>
</div>

### Working with message batches
Batches are similar to groups in that they represent collections of messages.
Batches even use a similar API to groups. However, batches differ from groups
in that they represent collections of output to all connections. In other words,
whereas groups are guaranteed to always be delivered to the same target component
instance, batches use normal selection routines to route each individual message.
Additionally, batches cannot be nested like groups, but groups can be contained
within batches. Batches simply represent windows of output from a port.

![Batches](http://s30.postimg.org/dwmiufo8x/groups_1.png)

The batch API works similarly to the group API, but batches are *not* named.

{% include snippet8.html ex="11" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex11-java">

{:.prettyprint .lang-java}
	output.port("out").batch(new Handler<OutputBatch>() {
	  public void handle(OutputBatch batch) {
	    batch.send("foo").send("bar").send("baz").end();
	  }
	});
	
</div>
<div class="tab-pane active" id="ex11-java8">

{:.prettyprint .lang-java}
	output.port("out").batch((batch) -> batch.send("foo").send("bar").send("baz").end());
	
</div>
<div class="tab-pane" id="ex11-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex11-javascript">
  
TODO
	
</div>
</div>

Just as with groups, batches need to be explicitly ended. However, only one batch
can be open for any given connection at any given time, so that means that a new
batch will not open until the previous batch has been ended.

On the input port side, the batch API works similarly to the group API.

{% include snippet8.html ex="12" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex12-java">

{:.prettyprint .lang-java}
	input.port("in").batchHandler(new Handler<InputBatch>() {
	  public void handle(InputBatch batch) {
	    // Aggregate all messages from the batch.
	    final JsonArray messages = new JsonArray();
	    batch.messageHandler(new Handler<String>() {
	      public void handle(String message) {
	        messages.add(message);
	      }
	    });
	
	    // Send the aggregated array once the batch is ended.
	    batch.endHandler(new Handler<Void>() {
	      public void handle(Void event) {
	        output.port("out").send(messages);
	      }
	    });
	  }
	});
	
</div>
<div class="tab-pane active" id="ex12-java8">

{:.prettyprint .lang-java}
	input.port("in").batchHandler((batch) -> {
	    // Aggregate all messages from the batch.
	    final JsonArray messages = new JsonArray();
	    batch.messageHandler((m) -> messages.add(message));
	
	    // Send the aggregated array once the batch is ended.
	    batch.endHandler((m) -> output.port("out").send(messages));
	});
	
</div>
<div class="tab-pane" id="ex12-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex12-javascript">
  
TODO
	
</div>
</div>

Batches cannot be nested, but they can contain groups.

{% include snippet8.html ex="13" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex13-java">

{:.prettyprint .lang-java}
	output.port("out").batch(new Handler<OutputBatch>() {
	  public void handle(OutputBatch batch) {
	    batch.group("fruits", new Handler<OutputGroup>() {
	      public void handle(OutputGroup group) {
	        group.send("apple").send("banana").send("peach").end();
	      }
	    });
	    batch.end();
	  }
	});
	
</div>
<div class="tab-pane active" id="ex13-java8">

{:.prettyprint .lang-java}
	output.port("out").batch((batch) -> {
	  batch.group("fruits", (group) -> group.send("apple").send("banana").send("peach").end());
	  batch.end();
	});
	
</div>
<div class="tab-pane" id="ex13-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex13-javascript">
  
TODO
	
</div>
</div>

Even if a batch is ended, it will not internally end and allow the next batch to
be created until any child groups have been successfully ended.

Groups within batches can be received in the same manner as they are with groups.

{% include snippet8.html ex="14" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex14-java">

{:.prettyprint .lang-java}
	input.port("in").batchHandler(new Handler<InputBatch>() {
	  public void handle(InputBatch batch) {
	    batch.groupHandler("fruits", new Handler<InputGroup>() {
	      public void handle(InputGroup group) {
	        final Set<String> fruits = new HashSet<>();
	        group.messageHandler(new Handler<String>() {
	          public void handle(String message) {
	            fruits.add(message);
	          }
	        });
	
	        group.endHandler(new Handler<Void>() {
	          public void handle(Void event) {
	            System.out.println("Got all the fruits!");
	          }
	        });
	      }
	    });
	  }
	});
	
</div>
<div class="tab-pane active" id="ex14-java8">

{:.prettyprint .lang-java}
	input.port("in").batchHandler((batch) -> {
	  batch.groupHandler("fruits", (group) -> {
	    final Set<String> fruits = new HashSet<>();
	    group.messageHandler((message) -> fruits.add(message));
	    group.endHandler((m) -> System.out.println("Got all the fruits!"));
	  });
	});
	
</div>
<div class="tab-pane" id="ex14-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex14-javascript">
  
TODO
	
</div>
</div>

### Providing serializable messages
In addition to types supported by the Vert.x event bus, the Vertigo messaging
framework supports any `Serializable` Java object.

{:.prettyprint .lang-java}
	public class MyMessage implements Serializeable {
	  private String foo;
	  private int bar;
	
	  public MyMessage(String foo, int bar) {
	    this.foo = foo;
	    this.bar = bar;
	  }
	}