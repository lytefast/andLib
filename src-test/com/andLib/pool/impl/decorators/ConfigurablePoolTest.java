package com.andLib.pool.impl.decorators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;

import com.andLib.pool.Pool;
import com.andLib.pool.PoolException;
import com.andLib.pool.Pools;

public class ConfigurablePoolTest {
  private static final int  CAPACITY = 1;
  private Provider<Object> providerSpy;

  @Before
  public void setUp() throws Exception {
    providerSpy = spy(new Provider<Object>() {
      @Override public Object get() {
        return new Object();
      }
    });
  }

  @Test
  public void forgivingPool_IgnoreRecycleErrors() {
    Pool<Object> pool = Pools.newForgivingPool(CAPACITY, providerSpy);
    populatePool(pool);
    assertFalse("additional elements ignored", pool.recycle(new Object()));
  }

  @Test
  public void forgivingPool_AlwaysReturnsResult() {
    Pool<Object> pool = Pools.newForgivingPool(CAPACITY, providerSpy);
    populatePool(pool);
    exhaustPool(pool);
    assertNotNull("obtain should be limitless", pool.obtain());
  }

  @Test(expected=PoolException.class)
  public void strictPool_IgnoreRecycleErrors() {
    Pool<Object> pool = Pools.newStrictPool(CAPACITY, providerSpy);
    populatePool(pool);
    pool.recycle(new Object());
  }

  @Test(expected=PoolException.class)
  public void strictPool_AlwaysReturnsResult() {
    Pool<Object> pool = Pools.newStrictPool(CAPACITY, providerSpy);
    populatePool(pool);
    exhaustPool(pool);
    pool.obtain();
  }

  private void populatePool(Pool<Object> pool) {
    for (int i = 0; i < CAPACITY; i++) {
      pool.recycle(providerSpy.get());
    }
  }

  private void exhaustPool(Pool<Object> pool) {
    for (int i = 0; i < CAPACITY; i++) {
      assertNotNull("there eshould be available poolables", pool.obtain());
    }
  }

}
