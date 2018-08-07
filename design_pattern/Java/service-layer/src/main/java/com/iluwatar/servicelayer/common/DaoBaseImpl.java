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
package com.iluwatar.servicelayer.common;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.iluwatar.servicelayer.hibernate.HibernateUtil;

/**
 * 
 * Base class for Dao implementations.
 *
 * @param <E>
 * 
 */
public abstract class DaoBaseImpl<E extends BaseEntity> implements Dao<E> {

  @SuppressWarnings("unchecked")
  protected Class<E> persistentClass = (Class<E>) ((ParameterizedType) getClass()
      .getGenericSuperclass()).getActualTypeArguments()[0];

  /*
   * Making this getSessionFactory() instead of getSession() so that it is the responsibility
   * of the caller to open as well as close the session (prevents potential resource leak).
   */
  protected SessionFactory getSessionFactory() {
    return HibernateUtil.getSessionFactory();
  }

  @Override
  public E find(Long id) {
    Session session = getSessionFactory().openSession();
    Transaction tx = null;
    E result = null;
    try {
      tx = session.beginTransaction();
      Criteria criteria = session.createCriteria(persistentClass);
      criteria.add(Restrictions.idEq(id));
      result = (E) criteria.uniqueResult();
      tx.commit();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw e;
    } finally {
      session.close();
    }
    return result;
  }

  @Override
  public void persist(E entity) {
    Session session = getSessionFactory().openSession();
    Transaction tx = null;
    try {
      tx = session.beginTransaction();
      session.persist(entity);
      tx.commit();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw e;
    } finally {
      session.close();
    }
  }

  @Override
  public E merge(E entity) {
    Session session = getSessionFactory().openSession();
    Transaction tx = null;
    E result = null;
    try {
      tx = session.beginTransaction();
      result = (E) session.merge(entity);
      tx.commit();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw e;
    } finally {
      session.close();
    }
    return result;
  }

  @Override
  public void delete(E entity) {
    Session session = getSessionFactory().openSession();
    Transaction tx = null;
    try {
      tx = session.beginTransaction();
      session.delete(entity);
      tx.commit();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw e;
    } finally {
      session.close();
    }
  }

  @Override
  public List<E> findAll() {
    Session session = getSessionFactory().openSession();
    Transaction tx = null;
    List<E> result = null;
    try {
      tx = session.beginTransaction();
      Criteria criteria = session.createCriteria(persistentClass);
      result = criteria.list();
    } catch (Exception e) {
      if (tx != null) {
        tx.rollback();
      }
      throw e;
    } finally {
      session.close();
    }
    return result;
  }
}
