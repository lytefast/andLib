package com.andLib.pool.impl.decorators;

import com.andLib.pool.Pool;

public class SynchronizedPool<T> implements Pool<T> {
  private final Pool<T> innerPool;

  public SynchronizedPool(Pool<T> innerPool) {
    this.innerPool = innerPool;
  }

  public static <T> SynchronizedPool<T> of(Pool<T> pool) {
    return new SynchronizedPool<>(pool);
  }

  @Override
  public synchronized T obtain() {
    return innerPool.obtain();
  }

  @Override
  public synchronized boolean recycle(T element) {
    return innerPool.recycle(element);
  }

  @Override
  public int usableCount() {
    return innerPool.usableCount();
  }
}
