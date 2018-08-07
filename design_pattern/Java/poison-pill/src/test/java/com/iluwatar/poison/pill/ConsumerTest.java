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
package com.iluwatar.poison.pill;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Date: 12/27/15 - 9:45 PM
 *
 * @author Jeroen Meulemeester
 */
public class ConsumerTest {

  private InMemoryAppender appender;

  @Before
  public void setUp() {
    appender = new InMemoryAppender(Consumer.class);
  }

  @After
  public void tearDown() {
    appender.stop();
  }

  @Test
  public void testConsume() throws Exception {
    final Message[] messages = new Message[]{
        createMessage("you", "Hello!"),
        createMessage("me", "Hi!"),
        Message.POISON_PILL,
        createMessage("late_for_the_party", "Hello? Anyone here?"),
    };

    final MessageQueue queue = new SimpleMessageQueue(messages.length);
    for (final Message message : messages) {
      queue.put(message);
    }

    new Consumer("NSA", queue).consume();

    assertTrue(appender.logContains("Message [Hello!] from [you] received by [NSA]"));
    assertTrue(appender.logContains("Message [Hi!] from [me] received by [NSA]"));
    assertTrue(appender.logContains("Consumer NSA receive request to terminate."));
  }

  /**
   * Create a new message from the given sender with the given message body
   *
   * @param sender  The sender's name
   * @param message The message body
   * @return The message instance
   */
  private static Message createMessage(final String sender, final String message) {
    final SimpleMessage msg = new SimpleMessage();
    msg.addHeader(Message.Headers.SENDER, sender);
    msg.addHeader(Message.Headers.DATE, LocalDateTime.now().toString());
    msg.setBody(message);
    return msg;
  }

  private class InMemoryAppender extends AppenderBase<ILoggingEvent> {
    private List<ILoggingEvent> log = new LinkedList<>();

    public InMemoryAppender(Class clazz) {
      ((Logger) LoggerFactory.getLogger(clazz)).addAppender(this);
      start();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
      log.add(eventObject);
    }

    public boolean logContains(String message) {
      return log.stream().anyMatch(event -> event.getFormattedMessage().equals(message));
    }
  }

}
