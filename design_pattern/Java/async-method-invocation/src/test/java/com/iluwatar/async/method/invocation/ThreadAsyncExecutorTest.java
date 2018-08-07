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
package com.iluwatar.async.method.invocation;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Date: 12/6/15 - 10:49 AM
 *
 * @author Jeroen Meulemeester
 */
public class ThreadAsyncExecutorTest {

  /**
   * Test used to verify the happy path of {@link ThreadAsyncExecutor#startProcess(Callable)}
   */
  @Test(timeout = 3000)
  public void testSuccessfulTaskWithoutCallback() throws Exception {
    // Instantiate a new executor and start a new 'null' task ...
    final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();

    final Object result = new Object();
    final Callable<Object> task = mock(Callable.class);
    when(task.call()).thenReturn(result);

    final AsyncResult<Object> asyncResult = executor.startProcess(task);
    assertNotNull(asyncResult);
    asyncResult.await(); // Prevent timing issues, and wait until the result is available
    assertTrue(asyncResult.isCompleted());

    // Our task should only execute once ...
    verify(task, times(1)).call();

    // ... and the result should be exactly the same object
    assertSame(result, asyncResult.getValue());
  }

  /**
   * Test used to verify the happy path of {@link ThreadAsyncExecutor#startProcess(Callable, AsyncCallback)}
   */
  @Test(timeout = 3000)
  public void testSuccessfulTaskWithCallback() throws Exception {
    // Instantiate a new executor and start a new 'null' task ...
    final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();

    final Object result = new Object();
    final Callable<Object> task = mock(Callable.class);
    when(task.call()).thenReturn(result);

    final AsyncCallback callback = mock(AsyncCallback.class);
    final AsyncResult<Object> asyncResult = executor.startProcess(task, callback);
    assertNotNull(asyncResult);
    asyncResult.await(); // Prevent timing issues, and wait until the result is available
    assertTrue(asyncResult.isCompleted());

    // Our task should only execute once ...
    verify(task, times(1)).call();

    // ... same for the callback, we expect our object
    final ArgumentCaptor<Optional<Exception>> optionalCaptor = ArgumentCaptor.forClass((Class) Optional.class);
    verify(callback, times(1)).onComplete(eq(result), optionalCaptor.capture());

    final Optional<Exception> optionalException = optionalCaptor.getValue();
    assertNotNull(optionalException);
    assertFalse(optionalException.isPresent());

    // ... and the result should be exactly the same object
    assertSame(result, asyncResult.getValue());
  }

  /**
   * Test used to verify the happy path of {@link ThreadAsyncExecutor#startProcess(Callable)} when a task takes a while
   * to execute
   */
  @Test(timeout = 5000)
  public void testLongRunningTaskWithoutCallback() throws Exception {
    // Instantiate a new executor and start a new 'null' task ...
    final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();

    final Object result = new Object();
    final Callable<Object> task = mock(Callable.class);
    when(task.call()).thenAnswer(i -> {
      Thread.sleep(1500);
      return result;
    });

    final AsyncResult<Object> asyncResult = executor.startProcess(task);
    assertNotNull(asyncResult);
    assertFalse(asyncResult.isCompleted());

    try {
      asyncResult.getValue();
      fail("Expected IllegalStateException when calling AsyncResult#getValue on a non-completed task");
    } catch (IllegalStateException e) {
      assertNotNull(e.getMessage());
    }

    // Our task should only execute once, but it can take a while ...
    verify(task, timeout(3000).times(1)).call();

    // Prevent timing issues, and wait until the result is available
    asyncResult.await();
    assertTrue(asyncResult.isCompleted());
    verifyNoMoreInteractions(task);

    // ... and the result should be exactly the same object
    assertSame(result, asyncResult.getValue());
  }

  /**
   * Test used to verify the happy path of {@link ThreadAsyncExecutor#startProcess(Callable, AsyncCallback)} when a task
   * takes a while to execute
   */
  @Test(timeout = 5000)
  public void testLongRunningTaskWithCallback() throws Exception {
    // Instantiate a new executor and start a new 'null' task ...
    final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();

    final Object result = new Object();
    final Callable<Object> task = mock(Callable.class);
    when(task.call()).thenAnswer(i -> {
      Thread.sleep(1500);
      return result;
    });

    final AsyncCallback<Object> callback = mock(AsyncCallback.class);
    final AsyncResult<Object> asyncResult = executor.startProcess(task, callback);
    assertNotNull(asyncResult);
    assertFalse(asyncResult.isCompleted());

    verifyZeroInteractions(callback);

    try {
      asyncResult.getValue();
      fail("Expected IllegalStateException when calling AsyncResult#getValue on a non-completed task");
    } catch (IllegalStateException e) {
      assertNotNull(e.getMessage());
    }

    // Our task should only execute once, but it can take a while ...
    verify(task, timeout(3000).times(1)).call();

    final ArgumentCaptor<Optional<Exception>> optionalCaptor = ArgumentCaptor.forClass((Class) Optional.class);
    verify(callback, timeout(3000).times(1)).onComplete(eq(result), optionalCaptor.capture());

    final Optional<Exception> optionalException = optionalCaptor.getValue();
    assertNotNull(optionalException);
    assertFalse(optionalException.isPresent());

    // Prevent timing issues, and wait until the result is available
    asyncResult.await();
    assertTrue(asyncResult.isCompleted());
    verifyNoMoreInteractions(task, callback);

    // ... and the result should be exactly the same object
    assertSame(result, asyncResult.getValue());
  }

  /**
   * Test used to verify the happy path of {@link ThreadAsyncExecutor#startProcess(Callable)} when a task takes a while
   * to execute, while waiting on the result using {@link ThreadAsyncExecutor#endProcess(AsyncResult)}
   */
  @Test(timeout = 5000)
  public void testEndProcess() throws Exception {
    // Instantiate a new executor and start a new 'null' task ...
    final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();

    final Object result = new Object();
    final Callable<Object> task = mock(Callable.class);
    when(task.call()).thenAnswer(i -> {
      Thread.sleep(1500);
      return result;
    });

    final AsyncResult<Object> asyncResult = executor.startProcess(task);
    assertNotNull(asyncResult);
    assertFalse(asyncResult.isCompleted());

    try {
      asyncResult.getValue();
      fail("Expected IllegalStateException when calling AsyncResult#getValue on a non-completed task");
    } catch (IllegalStateException e) {
      assertNotNull(e.getMessage());
    }

    assertSame(result, executor.endProcess(asyncResult));
    verify(task, times(1)).call();
    assertTrue(asyncResult.isCompleted());

    // Calling end process a second time while already finished should give the same result
    assertSame(result, executor.endProcess(asyncResult));
    verifyNoMoreInteractions(task);
  }

  /**
   * Test used to verify the behaviour of {@link ThreadAsyncExecutor#startProcess(Callable)} when the callable is 'null'
   */
  @Test(timeout = 3000)
  public void testNullTask() throws Exception {
    // Instantiate a new executor and start a new 'null' task ...
    final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
    final AsyncResult<Object> asyncResult = executor.startProcess(null);

    assertNotNull("The AsyncResult should not be 'null', even though the task was 'null'.", asyncResult);
    asyncResult.await(); // Prevent timing issues, and wait until the result is available
    assertTrue(asyncResult.isCompleted());

    try {
      asyncResult.getValue();
      fail("Expected ExecutionException with NPE as cause");
    } catch (final ExecutionException e) {
      assertNotNull(e.getMessage());
      assertNotNull(e.getCause());
      assertEquals(NullPointerException.class, e.getCause().getClass());
    }

  }

  /**
   * Test used to verify the behaviour of {@link ThreadAsyncExecutor#startProcess(Callable, AsyncCallback)} when the
   * callable is 'null', but the asynchronous callback is provided
   */
  @Test(timeout = 3000)
  public void testNullTaskWithCallback() throws Exception {
    // Instantiate a new executor and start a new 'null' task ...
    final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
    final AsyncCallback<Object> callback = mock(AsyncCallback.class);
    final AsyncResult<Object> asyncResult = executor.startProcess(null, callback);

    assertNotNull("The AsyncResult should not be 'null', even though the task was 'null'.", asyncResult);
    asyncResult.await(); // Prevent timing issues, and wait until the result is available
    assertTrue(asyncResult.isCompleted());

    final ArgumentCaptor<Optional<Exception>> optionalCaptor = ArgumentCaptor.forClass((Class) Optional.class);
    verify(callback, times(1)).onComplete(Matchers.isNull(), optionalCaptor.capture());

    final Optional<Exception> optionalException = optionalCaptor.getValue();
    assertNotNull(optionalException);
    assertTrue(optionalException.isPresent());

    final Exception exception = optionalException.get();
    assertNotNull(exception);
    assertEquals(NullPointerException.class, exception.getClass());

    try {
      asyncResult.getValue();
      fail("Expected ExecutionException with NPE as cause");
    } catch (final ExecutionException e) {
      assertNotNull(e.getMessage());
      assertNotNull(e.getCause());
      assertEquals(NullPointerException.class, e.getCause().getClass());
    }

  }

  /**
   * Test used to verify the behaviour of {@link ThreadAsyncExecutor#startProcess(Callable, AsyncCallback)} when both
   * the callable and the asynchronous callback are 'null'
   */
  @Test(timeout = 3000)
  public void testNullTaskWithNullCallback() throws Exception {
    // Instantiate a new executor and start a new 'null' task ...
    final ThreadAsyncExecutor executor = new ThreadAsyncExecutor();
    final AsyncResult<Object> asyncResult = executor.startProcess(null, null);

    assertNotNull("The AsyncResult should not be 'null', even though the task and callback were 'null'.", asyncResult);
    asyncResult.await(); // Prevent timing issues, and wait until the result is available
    assertTrue(asyncResult.isCompleted());

    try {
      asyncResult.getValue();
      fail("Expected ExecutionException with NPE as cause");
    } catch (final ExecutionException e) {
      assertNotNull(e.getMessage());
      assertNotNull(e.getCause());
      assertEquals(NullPointerException.class, e.getCause().getClass());
    }

  }

}