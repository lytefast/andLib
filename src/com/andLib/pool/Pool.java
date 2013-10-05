package com.andLib.pool;


/**
 * Contains a set of instances that can be reused.
 * <p>
 * Useful when instances require a lot of overhead to instantiate (i.e. types
 * which require a lot of heap space such as a large array). It is recommended
 * for {@link Pool}s to be used with
 * {@link com.andLib.pool.impl.decorators.CleaningPool} to keep dirty data from
 * escaping logical scopes.
 * 
 * @author lytefast
 * 
 * @param <T>
 *          type of instances to be pooled.
 * 
 * @see com.andLib.pool.impl.decorators.CleaningPool
 */
public interface Pool<T> {
  /**
   * @return an element that can be used.
   */
  T obtain();

  /**
   * @return if the pool was modified by this operation.
   */
  boolean recycle(T element);

  /**
   * @return number of poolables that are idle in the pool.
   */
  int usableCount();
}
