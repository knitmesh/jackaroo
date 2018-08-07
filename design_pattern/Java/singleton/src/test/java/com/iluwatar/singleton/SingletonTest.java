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
package com.iluwatar.singleton;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import org.junit.Test;

/**
 * This class provides several test case that test singleton construction.
 *
 * The first proves that multiple calls to the singleton getInstance object are the same when called
 * in the SAME thread. The second proves that multiple calls to the singleton getInstance object are
 * the same when called in the DIFFERENT thread.
 *
 * Date: 12/29/15 - 19:25 PM
 * @param <S> Supplier method generating singletons
 * @author Jeroen Meulemeester
 * @author Richard Jones
 */
public abstract class SingletonTest<S> {

  /**
   * The singleton's getInstance method
   */
  private final Supplier<S> singletonInstanceMethod;

  /**
   * Create a new singleton test instance using the given 'getInstance' method
   *
   * @param singletonInstanceMethod The singleton's getInstance method
   */
  public SingletonTest(final Supplier<S> singletonInstanceMethod) {
    this.singletonInstanceMethod = singletonInstanceMethod;
  }

  /**
   * Test the singleton in a non-concurrent setting
   */
  @Test
  public void testMultipleCallsReturnTheSameObjectInSameThread() {
    // Create several instances in the same calling thread
    S instance1 = this.singletonInstanceMethod.get();
    S instance2 = this.singletonInstanceMethod.get();
    S instance3 = this.singletonInstanceMethod.get();
    // now check they are equal
    assertSame(instance1, instance2);
    assertSame(instance1, instance3);
    assertSame(instance2, instance3);
  }

  /**
   * Test singleton instance in a concurrent setting
   */
  @Test(timeout = 10000)
  public void testMultipleCallsReturnTheSameObjectInDifferentThreads() throws Exception {

    // Create 10000 tasks and inside each callable instantiate the singleton class
    final List<Callable<S>> tasks = new ArrayList<>();
    for (int i = 0; i < 10000; i++) {
      tasks.add(this.singletonInstanceMethod::get);
    }

    // Use up to 8 concurrent threads to handle the tasks
    final ExecutorService executorService = Executors.newFixedThreadPool(8);
    final List<Future<S>> results = executorService.invokeAll(tasks);

    // wait for all of the threads to complete
    final S expectedInstance = this.singletonInstanceMethod.get();
    for (Future<S> res : results) {
      final S instance = res.get();
      assertNotNull(instance);
      assertSame(expectedInstance, instance);
    }

    // tidy up the executor
    executorService.shutdown();

  }

}
