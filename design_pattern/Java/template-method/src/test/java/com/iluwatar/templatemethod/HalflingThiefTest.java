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
package com.iluwatar.templatemethod;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Date: 12/29/15 - 18:15 PM
 *
 * @author Jeroen Meulemeester
 */
public class HalflingThiefTest {

  /**
   * Verify if the thief uses the provided stealing method
   */
  @Test
  public void testSteal() {
    final StealingMethod method = mock(StealingMethod.class);
    final HalflingThief thief = new HalflingThief(method);

    thief.steal();
    verify(method).steal();

    verifyNoMoreInteractions(method);
  }

  /**
   * Verify if the thief uses the provided stealing method, and the new method after changing it
   */
  @Test
  public void testChangeMethod() {
    final StealingMethod initialMethod = mock(StealingMethod.class);
    final HalflingThief thief = new HalflingThief(initialMethod);

    thief.steal();
    verify(initialMethod).steal();

    final StealingMethod newMethod = mock(StealingMethod.class);
    thief.changeMethod(newMethod);

    thief.steal();
    verify(newMethod).steal();

    verifyNoMoreInteractions(initialMethod, newMethod);

  }
}