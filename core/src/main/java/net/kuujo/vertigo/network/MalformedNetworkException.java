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
package net.kuujo.vertigo.network;

import net.kuujo.vertigo.VertigoException;

/**
 * A malformed definition exception.
 * 
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@SuppressWarnings("serial")
public class MalformedNetworkException extends VertigoException {

  public MalformedNetworkException(String message) {
    super(message);
  }

  public MalformedNetworkException(String message, Throwable cause) {
    super(message, cause);
  }

  public MalformedNetworkException(Throwable cause) {
    super(cause);
  }

}
