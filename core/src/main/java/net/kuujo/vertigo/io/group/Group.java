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
package net.kuujo.vertigo.io.group;

/**
 * Input/output group.<p>
 *
 * Groups are named logical collections of messages between two instances
 * of two components. When a group is created, the group will be assigned
 * to a single target instance per connection. Groups can be nested.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public interface Group<T extends Group<T>> {

  /**
   * Returns the unique group identifier.
   *
   * @return The unique group identifier.
   */
  String id();

  /**
   * Returns the group name.
   *
   * @return The group name.
   */
  String name();

}
