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
package com.iluwatar.servicelayer.spellbook;

import com.iluwatar.servicelayer.common.BaseDaoTest;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Date: 12/28/15 - 11:44 PM
 *
 * @author Jeroen Meulemeester
 */
public class SpellbookDaoImplTest extends BaseDaoTest<Spellbook, SpellbookDaoImpl> {

  public SpellbookDaoImplTest() {
    super(Spellbook::new, new SpellbookDaoImpl());
  }

  @Test
  public void testFindByName() throws Exception {
    final SpellbookDaoImpl dao = getDao();
    final List<Spellbook> allBooks = dao.findAll();
    for (final Spellbook book : allBooks) {
      final Spellbook spellByName = dao.findByName(book.getName());
      assertNotNull(spellByName);
      assertEquals(book.getId(), spellByName.getId());
      assertEquals(book.getName(), spellByName.getName());
    }
  }

}
