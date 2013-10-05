package com.andLib.pool.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class BasicPoolTest {
  private static final int CAPACITY = 2;
  private BasicPool<Object> pool;

  @Before
  public void setUp() throws Exception {
    pool = new BasicPool<>(CAPACITY);
  }

  @Test
  public void obtain_OnEmpty() {
    assertNull(pool.obtain());
  }

  @Test
  public void recycle_ObjectReused() {
    final Object cached = new Object();
    assertTrue("pool modified", pool.recycle(cached));
    assertEquals("same object returned", cached, pool.obtain());
  }
  @Test
  public void recycle_ObeysLimit() {
    for (int i = 0; i < CAPACITY; i++) {
      assertTrue("pool modified", pool.recycle(new Object()));
    }
    assertFalse("pool unchanged", pool.recycle(new Object()));
    assertEquals("only capacity available", CAPACITY, pool.usableCount());
  }
}
