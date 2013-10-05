package com.andLib.pool.impl.decorators;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.inject.Provider;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.andLib.pool.Pool;
import com.andLib.pool.impl.BasicPool;

public class LazyInitPoolTest {

  private static final int CAPACITY = 2;

  private Pool<Object> innerPoolSpy;
  private Provider<Object> providerSpy;
  private LazyInitPool<Object> pool;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    innerPoolSpy = spy(new BasicPool<>(CAPACITY));
    providerSpy = spy(new Provider<Object>() {
      @Override public Object get() {
        return new Object();
      }
    });
    pool = new LazyInitPool<Object>(CAPACITY, innerPoolSpy, providerSpy);
  }

  @Test
  public void obtain_onlyCreatesUpToCapacity() {
    for (int i = 0; i < CAPACITY; i++) {
      pool.obtain();
    }

    pool.obtain();
    verify(innerPoolSpy).obtain();
    verify(providerSpy, times(2)).get();
  }

  @Test
  public void obtain_createsLazily() {
    Object cached = pool.obtain();
    pool.recycle(cached);

    Assert.assertEquals("return recycled first", cached, pool.obtain());
    verify(innerPoolSpy).obtain();
    verify(providerSpy, times(1)).get();
  }
}
