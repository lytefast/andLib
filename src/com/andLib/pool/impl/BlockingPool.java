package com.andLib.pool.impl;

import java.util.concurrent.ArrayBlockingQueue;

import com.andLib.pool.Pool;
import com.andLib.pool.PoolException;
import com.andLib.pool.impl.decorators.SynchronizedPool;
import com.google.common.collect.Queues;

/**
 * Blocks if a request is made and no elements are found.
 * <p>
 * This class by itself is not thread safe; use in conjunction with
 * {@link SynchronizedPool} to ensure thread-safety.
 *
 * @author lytefast
 *
 * @param <T>
 */
public class BlockingPool<T> implements Pool<T> {
  protected final int                 capacity;
  private final ArrayBlockingQueue<T> available;

  public BlockingPool(int capacity) {
    this.capacity = capacity;
    this.available = Queues.newArrayBlockingQueue(capacity);
  }

  /**
   * @return an available element or blocks until one is available.
   *
   * @see com.andLib.pool.Pool#obtain()
   */
  @Override
  public T obtain() {
    try {
      return available.take();
    } catch (InterruptedException e) {
      throw new PoolException(e);
    }
  }

  @Override
  public boolean recycle(T element) {
    return available.offer(element);
  }

  @Override
  public int usableCount() {
    return available.size();
  }
}
