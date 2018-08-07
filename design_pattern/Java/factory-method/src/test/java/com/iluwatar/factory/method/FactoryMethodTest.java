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
package com.iluwatar.factory.method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * The Factory Method is a creational design pattern which uses factory methods to deal with the
 * problem of creating objects without specifying the exact class of object that will be created.
 * This is done by creating objects via calling a factory method either specified in an interface
 * and implemented by child classes, or implemented in a base class and optionally overridden by
 * derived classes—rather than by calling a constructor.
 * 
 * <p>Factory produces the object of its liking. 
 * The weapon {@link Weapon} manufactured by the
 * blacksmith depends on the kind of factory implementation it is referring to.
 * </p>
 */
public class FactoryMethodTest {

  /**
   * Testing {@link OrcBlacksmith} to produce a SPEAR asserting that the Weapon is an instance 
   * of {@link OrcWeapon}.
   */
  @Test
  public void testOrcBlacksmithWithSpear() {
    Blacksmith blacksmith = new OrcBlacksmith();
    Weapon weapon = blacksmith.manufactureWeapon(WeaponType.SPEAR);
    verifyWeapon(weapon, WeaponType.SPEAR, OrcWeapon.class);
  }

  /**
   * Testing {@link OrcBlacksmith} to produce a AXE asserting that the Weapon is an instance
   *  of {@link OrcWeapon}.
   */
  @Test
  public void testOrcBlacksmithWithAxe() {
    Blacksmith blacksmith = new OrcBlacksmith();
    Weapon weapon = blacksmith.manufactureWeapon(WeaponType.AXE);
    verifyWeapon(weapon, WeaponType.AXE, OrcWeapon.class);
  }

  /**
   * Testing {@link ElfBlacksmith} to produce a SHORT_SWORD asserting that the Weapon is an
   * instance of {@link ElfWeapon}.
   */
  @Test
  public void testElfBlacksmithWithShortSword() {
    Blacksmith blacksmith = new ElfBlacksmith();
    Weapon weapon = blacksmith.manufactureWeapon(WeaponType.SHORT_SWORD);
    verifyWeapon(weapon, WeaponType.SHORT_SWORD, ElfWeapon.class);
  }

  /**
   * Testing {@link ElfBlacksmith} to produce a SPEAR asserting that the Weapon is an instance
   * of {@link ElfWeapon}.
   */
  @Test
  public void testElfBlacksmithWithSpear() {
    Blacksmith blacksmith = new ElfBlacksmith();
    Weapon weapon = blacksmith.manufactureWeapon(WeaponType.SPEAR);
    verifyWeapon(weapon, WeaponType.SPEAR, ElfWeapon.class);
  }

  /**
   * This method asserts that the weapon object that is passed is an instance of the clazz and the
   * weapon is of type expectedWeaponType.
   * 
   * @param weapon weapon object which is to be verified
   * @param expectedWeaponType expected WeaponType of the weapon
   * @param clazz expected class of the weapon
   */
  private void verifyWeapon(Weapon weapon, WeaponType expectedWeaponType, Class<?> clazz) {
    assertTrue("Weapon must be an object of: " + clazz.getName(), clazz.isInstance(weapon));
    assertEquals("Weapon must be of weaponType: " + clazz.getName(), expectedWeaponType,
        weapon.getWeaponType());
  }
}
