package com.andLib.pool;

import javax.inject.Provider;

import com.andLib.pool.impl.BasicPool;
import com.andLib.pool.impl.BlockingPool;
import com.andLib.pool.impl.decorators.ConfigurablePool;
import com.andLib.pool.impl.decorators.LazyInitPool;
import com.andLib.pool.impl.decorators.SynchronizedPool;

public class Pools {

  private Pools() {};

  public static <T> Pool<T> newBlockingPool(int capacity, Provider<T> provider) {
    Pool<T> pool = new BlockingPool<>(capacity);
    return SynchronizedPool.of(pool);
  }

  public static <T> ConfigurablePool<T> newStrictPool(int capacity, Provider<T> provider) {
    Pool<T> pool = new BasicPool<>(capacity);
    return new ConfigurablePool<>(pool, new ConfigurablePool.StrictPoolStrategy<T>());
  }

  public static <T> ConfigurablePool<T> newForgivingPool(int capacity, Provider<T> provider) {
    Pool<T> pool = new BasicPool<>(capacity);
    return new ConfigurablePool<>(pool, new ConfigurablePool.ForgivingPoolStrategy<>(provider));
  }

  public static <T> Pool<T> newSimpleGrowingPool(int capacity, Provider<T> provider) {
    Pool<T> pool = newLazyInitForgivingPool(capacity, provider);
    return SynchronizedPool.of(pool);
  }

  public static <T> ConfigurablePool<T> newLazyInitForgivingPool(int capacity, Provider<T> provider) {
    Pool<T> pool = new BasicPool<>(capacity);
    pool = new LazyInitPool<>(capacity, pool, provider);
    return new ConfigurablePool<>(pool, new ConfigurablePool.ForgivingPoolStrategy<>(provider));
  }

  public static <T> ConfigurablePool<T> newLazyInitForgivingPool(int capacity,
      Provider<T> provider, Runnable fullyInitializedCallback) {
    Pool<T> pool = new BasicPool<>(capacity);
    pool = new LazyInitPool<>(capacity, pool, provider, fullyInitializedCallback);
    return new ConfigurablePool<>(pool, new ConfigurablePool.ForgivingPoolStrategy<>(provider));
  }
}
