/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Seppälä
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.featuretoggle.pattern.tieredversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.iluwatar.featuretoggle.pattern.Service;
import com.iluwatar.featuretoggle.user.User;
import com.iluwatar.featuretoggle.user.UserGroup;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Tiered Feature Toggle
 */
public class TieredFeatureToggleVersionTest {

  final User paidUser = new User("Jamie Coder");
  final User freeUser = new User("Alan Defect");
  final Service service = new TieredFeatureToggleVersion();

  @Before
  public void setUp() throws Exception {
    UserGroup.addUserToPaidGroup(paidUser);
    UserGroup.addUserToFreeGroup(freeUser);
  }

  @Test
  public void testGetWelcomeMessageForPaidUser() throws Exception {
    final String welcomeMessage = service.getWelcomeMessage(paidUser);
    final String expected = "You're amazing Jamie Coder. Thanks for paying for this awesome software.";
    assertEquals(expected, welcomeMessage);
  }

  @Test
  public void testGetWelcomeMessageForFreeUser() throws Exception {
    final String welcomeMessage = service.getWelcomeMessage(freeUser);
    final String expected = "I suppose you can use this software.";
    assertEquals(expected, welcomeMessage);
  }

  @Test
  public void testIsEnhancedAlwaysTrueAsTiered() throws Exception {
    assertTrue(service.isEnhanced());
  }
}
