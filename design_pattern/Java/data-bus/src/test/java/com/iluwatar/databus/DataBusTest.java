/**
 * The MIT License
 * Copyright (c) 2014 Ilkka Seppälä
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
package com.iluwatar.databus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/**
 * Tests for {@link DataBus}.
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
public class DataBusTest {

  @Mock
  private Member member;

  @Mock
  private DataType event;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void publishedEventIsReceivedBySubscribedMember() {
    //given
    final DataBus dataBus = DataBus.getInstance();
    dataBus.subscribe(member);
    //when
    dataBus.publish(event);
    //then
    then(member).should().accept(event);
  }

  @Test
  public void publishedEventIsNotReceivedByMemberAfterUnsubscribing() {
    //given
    final DataBus dataBus = DataBus.getInstance();
    dataBus.subscribe(member);
    dataBus.unsubscribe(member);
    //when
    dataBus.publish(event);
    //then
    then(member).should(never()).accept(event);
  }

}
