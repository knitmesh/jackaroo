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
package com.iluwatar.promise;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A really simplified implementation of future that allows completing it successfully with a value 
 * or exceptionally with an exception.
 */
class PromiseSupport<T> implements Future<T> {

  private static final int RUNNING = 1;
  private static final int FAILED = 2;
  private static final int COMPLETED = 3;

  private final Object lock;

  private volatile int state = RUNNING;
  private T value;
  private Exception exception;

  PromiseSupport() {
    this.lock = new Object();
  }

  void fulfill(T value) {
    this.value = value;
    this.state = COMPLETED;
    synchronized (lock) {
      lock.notifyAll();
    }
  }

  void fulfillExceptionally(Exception exception) {
    this.exception = exception;
    this.state = FAILED;
    synchronized (lock) {
      lock.notifyAll();
    }
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public boolean isDone() {
    return state > RUNNING;
  }

  @Override
  public T get() throws InterruptedException, ExecutionException {
    if (state == COMPLETED) {
      return value;
    } else if (state == FAILED) {
      throw new ExecutionException(exception);
    } else {
      synchronized (lock) {
        lock.wait();
        if (state == COMPLETED) {
          return value;
        } else {
          throw new ExecutionException(exception);
        }
      }
    }
  }

  @Override
  public T get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    if (state == COMPLETED) {
      return value;
    } else if (state == FAILED) {
      throw new ExecutionException(exception);
    } else {
      synchronized (lock) {
        lock.wait(unit.toMillis(timeout));
        if (state == COMPLETED) {
          return value;
        } else if (state == FAILED) {
          throw new ExecutionException(exception);
        } else {
          throw new TimeoutException();
        }
      }
    }
  }
}