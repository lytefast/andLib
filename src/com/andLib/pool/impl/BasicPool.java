package com.andLib.pool.impl;

import java.util.Stack;

import com.andLib.pool.Pool;

/**
 * Basic limited pool which just holds a set of recycled instances, and returns
 * on a best effort basis.
 * <p>
 * Offers no synchronization, and therefore not thread-safe.
 *
 * @author lytefast
 *
 * @param <T>
 */
public class BasicPool<T> implements Pool<T> {
  protected final int capacity;
  private final Stack<T> available;

  public BasicPool(int capacity) {
    this.capacity = capacity;
    this.available = new Stack<>();
  }

  /**
   * @return an available element or {@code null} (for efficiency sake) if there
   *         are no available elements in the pool.
   *
   * @see com.andLib.pool.Pool#obtain()
   */
  @Override
  public T obtain() {
    if (available.isEmpty()) {
      return null;
    }
    return available.pop();
  }

  @Override
  public boolean recycle(T element) {
    if (available.size() < capacity) {
      return available.add(element);
    }
    return false;
  }

  @Override
  public int usableCount() {
    return available.size();
  }
}
