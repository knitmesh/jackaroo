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
package com.iluwatar.servant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Servant offers some functionality to a group of classes without defining that functionality in
 * each of them. A Servant is a class whose instance provides methods that take care of a desired
 * service, while objects for which the servant does something, are taken as parameters.
 * <p>
 * In this example {@link Servant} is serving {@link King} and {@link Queen}.
 *
 */
public class App {

  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  static Servant jenkins = new Servant("Jenkins");
  static Servant travis = new Servant("Travis");

  /**
   * Program entry point
   */
  public static void main(String[] args) {
    scenario(jenkins, 1);
    scenario(travis, 0);
  }

  /**
   * Can add a List with enum Actions for variable scenarios
   */
  public static void scenario(Servant servant, int compliment) {
    King k = new King();
    Queen q = new Queen();

    List<Royalty> guests = new ArrayList<>();
    guests.add(k);
    guests.add(q);

    // feed
    servant.feed(k);
    servant.feed(q);
    // serve drinks
    servant.giveWine(k);
    servant.giveWine(q);
    // compliment
    servant.giveCompliments(guests.get(compliment));

    // outcome of the night
    for (Royalty r : guests) {
      r.changeMood();
    }

    // check your luck
    if (servant.checkIfYouWillBeHanged(guests)) {
      LOGGER.info("{} will live another day", servant.name);
    } else {
      LOGGER.info("Poor {}. His days are numbered", servant.name);
    }
  }
}
