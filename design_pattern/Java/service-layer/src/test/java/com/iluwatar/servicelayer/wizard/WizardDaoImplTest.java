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
package com.iluwatar.servicelayer.wizard;

import com.iluwatar.servicelayer.common.BaseDaoTest;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Date: 12/28/15 - 11:46 PM
 *
 * @author Jeroen Meulemeester
 */
public class WizardDaoImplTest extends BaseDaoTest<Wizard, WizardDaoImpl> {

  public WizardDaoImplTest() {
    super(Wizard::new, new WizardDaoImpl());
  }

  @Test
  public void testFindByName() throws Exception {
    final WizardDaoImpl dao = getDao();
    final List<Wizard> allWizards = dao.findAll();
    for (final Wizard spell : allWizards) {
      final Wizard byName = dao.findByName(spell.getName());
      assertNotNull(byName);
      assertEquals(spell.getId(), byName.getId());
      assertEquals(spell.getName(), byName.getName());
    }
  }

}
