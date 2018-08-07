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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * Thread Pool pattern is where a number of threads are created to perform a number of tasks, which
 * are usually organized in a queue. The results from the tasks being executed might also be placed
 * in a queue, or the tasks might return no result. Typically, there are many more tasks than
 * threads. As soon as a thread completes its task, it will request the next task from the queue
 * until all tasks have been completed. The thread can then terminate, or sleep until there are new
 * tasks available.
 * <p>
 * In this example we create a list of tasks presenting work to be done. Each task is then wrapped
 * into a {@link Worker} object that implements {@link Runnable}. We create an
 * {@link ExecutorService} with fixed number of threads (Thread Pool) and use them to execute the
 * {@link Worker}s.
 *
 */
public class App {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  /**
   * Program entry point
   * 
   * @param args command line args
   */
  public static void main(String[] args) {

    LOGGER.info("Program started");

    // Create a list of tasks to be executed
    List<Task> tasks = new ArrayList<>();
    tasks.add(new PotatoPeelingTask(3));
    tasks.add(new PotatoPeelingTask(6));
    tasks.add(new CoffeeMakingTask(2));
    tasks.add(new CoffeeMakingTask(6));
    tasks.add(new PotatoPeelingTask(4));
    tasks.add(new CoffeeMakingTask(2));
    tasks.add(new PotatoPeelingTask(4));
    tasks.add(new CoffeeMakingTask(9));
    tasks.add(new PotatoPeelingTask(3));
    tasks.add(new CoffeeMakingTask(2));
    tasks.add(new PotatoPeelingTask(4));
    tasks.add(new CoffeeMakingTask(2));
    tasks.add(new CoffeeMakingTask(7));
    tasks.add(new PotatoPeelingTask(4));
    tasks.add(new PotatoPeelingTask(5));

    // Creates a thread pool that reuses a fixed number of threads operating off a shared
    // unbounded queue. At any point, at most nThreads threads will be active processing
    // tasks. If additional tasks are submitted when all threads are active, they will wait
    // in the queue until a thread is available.
    ExecutorService executor = Executors.newFixedThreadPool(3);

    // Allocate new worker for each task
    // The worker is executed when a thread becomes
    // available in the thread pool
    for (int i = 0; i < tasks.size(); i++) {
      Runnable worker = new Worker(tasks.get(i));
      executor.execute(worker);
    }
    // All tasks were executed, now shutdown
    executor.shutdown();
    while (!executor.isTerminated()) {
      Thread.yield();
    }
    LOGGER.info("Program finished");
  }
}
