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
package com.iluwatar.hexagonal.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.iluwatar.hexagonal.domain.LotteryNumbers;
import com.iluwatar.hexagonal.domain.LotteryTicket;
import com.iluwatar.hexagonal.domain.LotteryTicketId;
import com.iluwatar.hexagonal.domain.PlayerDetails;

/**
 * 
 * Utilities for lottery tests
 *
 */
public class LotteryTestUtils {

  /**
   * @return lottery ticket
   */
  public static LotteryTicket createLotteryTicket() {
    return createLotteryTicket("foo@bar.com", "12231-213132", "+99324554", new HashSet<>(Arrays.asList(1, 2, 3, 4)));
  }
  
  /**
   * @return lottery ticket
   */
  public static LotteryTicket createLotteryTicket(String email, String account, String phone,
      Set<Integer> givenNumbers) {
    PlayerDetails details = new PlayerDetails(email, account, phone);
    LotteryNumbers numbers = LotteryNumbers.create(givenNumbers);
    return new LotteryTicket(new LotteryTicketId(), details, numbers);
  }
}
