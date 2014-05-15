---
layout: content
menu: user-manual
title: Hooks
---

# Hooks

* [InputHook](#inputhook)
* [OutputHook](#outputhook)
* [IOHook](#iohook)
* [ComponentHook](#componenthook)

## Hooks
Vertigo provides a special mechanism for hooking into component and messaging
events. Hooks are objects that are added to the network configuration and receive
notifications when certain events occur. All hooks implement the `JsonSerializable`
interface which handles JSON serialization with Jackson. In most cases, users
can simply implement the relevant hook interface and Vertigo will handle serialization
of basic fields automatically. You can use Jackson annotations to ignore certain
fields or provide custom serialization as necessary.

### InputHook
Input hooks can be used to hook into message events related to the target port
on a specific connection within a network configuration. Input hooks are added
to the `target` element of a `ConnectionConfig`.

To define an input hook implement the `InputHook` interface.

{% include snippet.html ex="1" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex1-java">

{:.prettyprint .lang-java}
	public class MyInputHook implements InputHook {
	  @Override
	  public void handleReceive(Object message) {
	  
	  }
	}
	
</div>
<div class="tab-pane" id="ex1-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex1-javascript">
  
TODO
	
</div>
</div>

The `InputHook` interface requires only a single `handleReceive` method which
will be called whenever a message is received on the connection.

To add the hook to a connection use the `addHook` method on a `ConnectionConfig.Target`
instance.

{% include snippet.html ex="2" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex2-java">

{:.prettyprint .lang-java}
	network.createConnection("foo", "out", "bar", "in").getTarget().addHook(new MyInputHook());
	
</div>
<div class="tab-pane" id="ex2-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex2-javascript">
  
TODO
	
</div>
</div>

### OutputHook
Output hooks can be used to hook into message events related to the source port
on a specific connection within a network configuration. Output hooks are added
to the `source` element of a `ConnectionConfig`.

To define an output hook implement the `OutputHook` interface.

{% include snippet.html ex="3" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex3-java">

{:.prettyprint .lang-java}
	public class MyOutputHook implements OutputHook {
	  @Override
	  public void handleSend(Object message) {
	  
	  }
	}
	
</div>
<div class="tab-pane" id="ex3-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex3-javascript">
  
TODO
	
</div>
</div>

The `OutputHook` interface requires only a single `handleSend` method which
will be called whenever a message is sent on the connection.

To add the hook to a connection use the `addHook` method on a `ConnectionConfig.Source`
instance.

{% include snippet.html ex="4" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex4-java">

{:.prettyprint .lang-java}
	network.createConnection("foo", "out", "bar", "in").getSource().addHook(new MyOutputHook());
	
</div>
<div class="tab-pane" id="ex4-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex4-javascript">
  
TODO
	
</div>
</div>

### IOHook
I/O hooks are a combination of the `InputHook` and `OutputHook` interfaces. When an
I/O hook is added to a connection, its methods will be called when an event occurs
on either side of the connection. For the sending side of the connection, the hook
will be called when a message is sent, and for the receiving side of the connection,
the hook will be called when a message is received.

{% include snippet.html ex="5" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex5-java">

{:.prettyprint .lang-java}
	public class MyIOHook implements IOHook {
	  @Override
	  public void handleSend(Object message) {
	  
	  }
	
	  @Override
	  public void handleReceive(Object message) {
	  
	  }
	}
	
</div>
<div class="tab-pane" id="ex5-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex5-javascript">
  
TODO
	
</div>
</div>

To add the hook to a connection use the `addHook` method on a `ConnectionConfig` instance.

{% include snippet.html ex="6" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex6-java">

{:.prettyprint .lang-java}
	network.createConnection("foo", "out", "bar", "in").addHook(new MyIOHook());
	
</div>
<div class="tab-pane" id="ex6-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex6-javascript">
  
TODO
	
</div>
</div>

### ComponentHook
Components hooks are component-level hooks that implement both the `InputHook`
and `OutputHook` interfaces along with some additional component-level hook methods.
Since component hooks are added at the component level, the `handleSend` and
`handleReceive` methods will be called each time a message is sent or received
on *any* port. Additionally, the component hook can receive notifications of when
the component has started and stopped as well.

{% include snippet.html ex="7" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex7-java">

{:.prettyprint .lang-java}
	public class MyComponentHook implements ComponentHook {
	  @Override
	  public void handleStart(Component component) {
	  
	  }
	
	  @Override
	  public void handleSend(Object message) {
	  
	  }
	
	  @Override
	  public void handleReceive(Object message) {
	  
	  }
	
	  @Override
	  public void handleStop(Component component) {
	  
	  }
	}
	
</div>
<div class="tab-pane" id="ex7-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex7-javascript">
  
TODO
	
</div>
</div>

The `handleStart` and `handleStop` will be passed the internal Vertigo
`Component` instance which contains all fields available to the Java `ComponentVerticle`
along with additional information about the component configuration.

Component hooks are added to `ComponentConfig` instances within the network configuration.

{% include snippet.html ex="8" %}
{::options parse_block_html="true" /}
<div class="tab-content">
<div class="tab-pane active" id="ex8-java">

{:.prettyprint .lang-java}
	network.addComponent("foo", "foo.js", 2).addHook(new MyComponentHook());
	
</div>
<div class="tab-pane" id="ex8-python">
  
TODO
	
</div>
<div class="tab-pane" id="ex8-javascript">
  
TODO
	
</div>
</div>