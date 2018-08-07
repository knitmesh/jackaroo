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
package com.iluwatar.threadpool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import org.junit.Test;

/**
 * Date: 12/30/15 - 18:22 PM
 * Test for Tasks using a Thread Pool
 * @param <T> Type of Task
 * @author Jeroen Meulemeester
 */
public abstract class TaskTest<T extends Task> {

  /**
   * The number of tasks used during the concurrency test
   */
  private static final int TASK_COUNT = 128 * 1024;

  /**
   * The number of threads used during the concurrency test
   */
  private static final int THREAD_COUNT = 8;

  /**
   * The task factory, used to create new test items
   */
  private final IntFunction<T> factory;

  /**
   * The expected time needed to run the task 1 single time, in milli seconds
   */
  private final int expectedExecutionTime;

  /**
   * Create a new test instance
   *
   * @param factory               The task factory, used to create new test items
   * @param expectedExecutionTime The expected time needed to run the task 1 time, in milli seconds
   */
  public TaskTest(final IntFunction<T> factory, final int expectedExecutionTime) {
    this.factory = factory;
    this.expectedExecutionTime = expectedExecutionTime;
  }

  /**
   * Verify if the generated id is unique for each task, even if the tasks are created in separate
   * threads
   */
  @Test(timeout = 10000)
  public void testIdGeneration() throws Exception {
    final ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);

    final List<Callable<Integer>> tasks = new ArrayList<>();
    for (int i = 0; i < TASK_COUNT; i++) {
      tasks.add(() -> factory.apply(1).getId());
    }

    final List<Integer> ids = service.invokeAll(tasks)
        .stream()
        .map(TaskTest::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    service.shutdownNow();

    final long uniqueIdCount = ids.stream()
        .distinct()
        .count();

    assertEquals(TASK_COUNT, ids.size());
    assertEquals(TASK_COUNT, uniqueIdCount);

  }

  /**
   * Verify if the time per execution of a task matches the actual time required to execute the task
   * a given number of times
   */
  @Test
  public void testTimeMs() {
    for (int i = 0; i < 10; i++) {
      assertEquals(this.expectedExecutionTime * i, this.factory.apply(i).getTimeMs());
    }
  }

  /**
   * Verify if the task has some sort of {@link T#toString()}, different from 'null'
   */
  @Test
  public void testToString() {
    assertNotNull(this.factory.apply(0).toString());
  }

  /**
   * Extract the result from a future or returns 'null' when an exception occurred
   *
   * @param future The future we want the result from
   * @param <O>    The result type
   * @return The result or 'null' when a checked exception occurred
   */
  private static <O> O get(Future<O> future) {
    try {
      return future.get();
    } catch (InterruptedException | ExecutionException e) {
      return null;
    }
  }

}
