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

package com.iluwatar.event.queue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Event or message queues provide an asynchronous communications protocol, meaning that the sender
 * and receiver of the message do not need to interact with the message queue at the same time. 
 * Events or messages placed onto the queue are stored until the recipient retrieves them. Event 
 * or message queues have implicit or explicit limits on the size of data that may be transmitted
 * in a single message and the number of messages that may remain outstanding on the queue.
 * A queue stores a series of notifications or requests in first-in, first-out order.
 * Sending a notification enqueues the request and returns. The request processor then processes
 * items from the queue at a later time.
 */
public class App {
  /**
   * Program entry point.
   * 
   * @param args command line args
   * @throws IOException when there is a problem with the audio file loading
   * @throws UnsupportedAudioFileException  when the loaded audio file is unsupported 
   */
  public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
    Audio.playSound(Audio.getAudioStream("./etc/Bass-Drum-1.wav"), -10.0f);
    Audio.playSound(Audio.getAudioStream("./etc/Closed-Hi-Hat-1.wav"), -8.0f);
    
    System.out.println("Press Enter key to stop the program...");
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    br.read();
    Audio.stopService();
  }
}
