---
layout: content
menu: examples
title: Flattening
---

# Flattening

These examples demonstrate the flattening of a complex object model to a more simple model.

## Example 1

Consider the following complex object model:

{:.prettyprint .lang-java}
	// Assume getters and setters on each class
	
	public class Order {
	  private Customer customer;
	  private Address shippingAddress;
	  private Address billingAddress;
	}
	
	public class Customer {
	  private String name;
	}
	
	public class Address {
	  private String street;
	  private String city;
	}

We may wish to flatten `Order` to a single object:

{:.prettyprint .lang-java}
	public class OrderDTO {
	  private String customerName;
	  private String shippingStreetAddress;
	  private String shippingCity;
	  private String billingStreetAddress;
	  private String billingCity;
	
	  // Assume getters and setters
	}

Assuming we have an `order` object we'd like to map to an `OrderDTO`, performing the mapping is simple:

{:.prettyprint .lang-java}
	ModelMapper modelMapper = new ModelMapper();
	OrderDTO dto = modelMapper.map(order, OrderDTO.class);

We can assert that the values were mapped as expected:

{:.prettyprint .lang-java}
	assert dto.getCustomerName().equals(order.getCustomer().getName());
	assert dto.getShippingStreetAddress().equals(order.getShippingAddress().getStreet());
	assert dto.getShippingCity().equals(order.getShippingAddress().getCity());
	assert dto.getBillingStreetAddress().equals(order.getBillingAddress().getStreet());
	assert dto.getBillingCity().equals(order.getBillingAddress().getCity());

## Example 2

Consider the following source object model:

{:.prettyprint .lang-java}
	// Assume getters and setters on each class
	
	public class Person {
	  private Address address;
	}
	
	public class Address {
	  private String street;
	  private String city;
	}

We may wish to flatten `Person` to the following object:

{:.prettyprint .lang-java}
	public class PersonDTO {
	  private String street;
	  private String city;
	
	  // Assume getters and setters
	}

With the default (Standard) [matching strategy](/user-manual/configuration/#matching-strategies), `Person.address.street` will not match `PersonDTO.street` and `Person.address.city` will not match `PersonDTO.city` since the expected `address` token is not present on the destination side. To solve this we have two options:

### Option 1: Create a PropertyMap

A PropertyMap allows us to create explicit mappings for `street` and `city` between the source and destination types:

{:.prettyprint .lang-java}
	PropertyMap<Person, PersonDTO> personMap = new PropertyMap<Person, PersonDTO>() {
	  protected void configure() {
	    map().setStreet(source.getAddress().getStreet());
	    map().setCity(source.getAddress().getCity());
	  }
	};
	
	modelMapper.addMappings(personMap);

### Option 2: Use the Loose Matching Strategy

While the default Standard [matching strategy](/user-manual/configuration/#matching-strategies) will not match source and destination properties that are missing a name token, the Loose matching strategy will match properties so long as the _last_ destination property name in the hierarchy is matched. In this case the last destination property name tokens are `street` and `city` which we can expect a match for since they are present on the source side.

Configuring the Loose matching strategy to be used is simple:

{:.prettyprint .lang-java}
	modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

### Result

After loading a PropertyMap or setting the Loose matching strategy, we can perform the map operation and assert that our results are as expected:

{:.prettyprint .lang-java}
	PersonDTO dto = modelMapper.map(person, PersonDTO.class);
	
	assert dto.getStreet().equals(person.getAddress().getStreet());
	assert dto.getCity().equals(person.getAddress().getCity());