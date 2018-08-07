/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.monad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The Monad pattern defines a monad structure, that enables chaining operations
 * in pipelines and processing data step by step.
 * Formally, monad consists of a type constructor M and two operations:
 * <br>bind - that takes monadic object and a function from plain object to the
 * monadic value and returns monadic value.
 * <br>return - that takes plain type object and returns this object wrapped in a monadic value.
 * <p>
 * In the given example, the Monad pattern is represented as a {@link Validator} that takes an instance
 * of a plain object with {@link Validator#of(Object)}
 * and validates it {@link Validator#validate(Function, Predicate, String)} against given predicates.
 * <p>As a validation result {@link Validator#get()} it either returns valid object {@link Validator#t}
 * or throws a list of exceptions {@link Validator#exceptions} collected during validation.
 */
public class App {

  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  /**
   * Program entry point.
   *
   * @param args @param args command line args
   */
  public static void main(String[] args) {
    User user = new User("user", 24, Sex.FEMALE, "foobar.com");
    LOGGER.info(Validator.of(user).validate(User::getName, Objects::nonNull, "name is null")
        .validate(User::getName, name -> !name.isEmpty(), "name is empty")
        .validate(User::getEmail, email -> !email.contains("@"), "email doesn't containt '@'")
        .validate(User::getAge, age -> age > 20 && age < 30, "age isn't between...").get().toString());
  }
}
