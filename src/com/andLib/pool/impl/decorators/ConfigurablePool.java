package com.andLib.pool.impl.decorators;

import javax.inject.Provider;

import com.andLib.pool.Pool;
import com.andLib.pool.PoolException;

/**
 * {@link Pool} decorator to throw an exception if there are no available
 * elements.
 *
 * @author lytefast
 *
 * @param <T>
 */
public class ConfigurablePool<T> implements Pool<T> {
  private final Pool<T>         innerPool;
  private final PoolStrategy<T> strategy;

  public ConfigurablePool(Pool<T> innerPool, PoolStrategy<T> strategy) {
    this.innerPool = innerPool;
    this.strategy = strategy;
  }

  @Override
  public T obtain() throws PoolException {
    T element = innerPool.obtain();
    if (element == null) {
      return strategy.onPoolExhausted();
    }
    return element;
  }

  @Override
  public boolean recycle(T element) throws PoolException {
    boolean isModified = innerPool.recycle(element);
    if (!isModified) {
      strategy.onNoImpactRecycle();
    }
    return isModified;
  }

  @Override
  public int usableCount() {
    return innerPool.usableCount();
  }

  /*
   * == Strategy implementations =============
   */

  public interface PoolStrategy<T> {
    T onPoolExhausted() throws PoolException;

    boolean onNoImpactRecycle() throws PoolException;
  }

  public static class ForgivingPoolStrategy<T> implements PoolStrategy<T> {
    private final Provider<T> provider;

    public ForgivingPoolStrategy(Provider<T> provider) {
      this.provider = provider;
    }

    @Override
    public T onPoolExhausted() throws PoolException {
      return provider.get();
    }

    @Override
    public boolean onNoImpactRecycle() throws PoolException {
      return false;
    }
  }

  public static class StrictPoolStrategy<T> implements PoolStrategy<T> {
    public StrictPoolStrategy() {
    }

    @Override
    public T onPoolExhausted() throws PoolException {
      throw new PoolException("Pool Exhausted");
    }

    @Override
    public boolean onNoImpactRecycle() throws PoolException {
      throw new PoolException("Returned poolable had no effect");
    }
  }
}
