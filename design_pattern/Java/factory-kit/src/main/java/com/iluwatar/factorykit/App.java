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
package com.iluwatar.factorykit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory-kit is a creational pattern which defines a factory of immutable content
 * with separated builder and factory interfaces to deal with the problem of
 * creating one of the objects specified directly in the factory-kit instance.
 *
 * <p>
 * In the given example {@link WeaponFactory} represents the factory-kit, that contains
 * four {@link Builder}s for creating new objects of
 * the classes implementing {@link Weapon} interface.
 * <br>Each of them can be called with {@link WeaponFactory#create(WeaponType)} method, with
 * an input representing an instance of {@link WeaponType} that needs to
 * be mapped explicitly with desired class type in the factory instance.
 */
public class App {

  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  /**
   * Program entry point.
   *
   * @param args @param args command line args
   */
  public static void main(String[] args) {
    WeaponFactory factory = WeaponFactory.factory(builder -> {
      builder.add(WeaponType.SWORD, Sword::new);
      builder.add(WeaponType.AXE, Axe::new);
      builder.add(WeaponType.SPEAR, Spear::new);
      builder.add(WeaponType.BOW, Bow::new);
    });
    Weapon axe = factory.create(WeaponType.AXE);
    LOGGER.info(axe.toString());
  }
}
