package com.andLib.pool.impl.decorators;

import com.andLib.pool.Pool;
import com.andLib.pool.impl.decorators.CleaningPool.Poolable;

/**
 * {@link Pool} decorator which ensures that elements returned to the pool are
 * cleaned.
 * 
 * @author lytefast
 * 
 * @param <T>
 */
public class CleaningPool<T extends Poolable> implements Pool<T>{
  private final Pool<T> innerPool;

  interface Poolable {
    /**
     * Remove any data which may linger in the object or may be misinterpreted
     * by new Poolable users.
     */
    void clean();
  }

  public CleaningPool(Pool<T> innerPool) {
    this.innerPool = innerPool;
  }
  
  @Override
  public T obtain() {
    return innerPool.obtain();
  }

  @Override
  public boolean recycle(T element) {
    element.clean();
    return innerPool.recycle(element);
  }
  
  @Override
  public int usableCount() {
    return innerPool.usableCount();
  }
}
