package com.andLib.pool.impl.decorators;

import javax.inject.Provider;

import com.andLib.pool.Pool;

/**
 * Automatically create elements for the pool when needed.
 * <p>
 * Users should note that this class causes an unnecessary layer after it's fully initialized the pool.
 * Removal of this pool after fully initialized is recommended. See code below for usage. 
 * <p>
 * Use:
 * <pre> {@code
 *   MyClass.this.actualPool = new LazyInitPool(2, innerPool, provider, new Runnable() {
 *     @Override public void run() {
 *       MyClass.this.actualPool = innerPool;
 *     }
 *   });
 * }</pre>
 * 
 * @author lytefast
 * 
 * @param <T>
 */
public class LazyInitPool<T> implements Pool<T> {

  private final Pool<T>     innerPool;
  private Provider<T> provider;

  public LazyInitPool(int capacity, Pool<T> innerPool, Provider<T> provider) {
    this(capacity, innerPool, provider, null);
  }

  public LazyInitPool(int capacity, Pool<T> innerPool, Provider<T> provider,
      Runnable fullyInitializedListener) {
    this.innerPool = innerPool;
    this.provider = createLazyInitProvider(capacity, innerPool, provider, fullyInitializedListener);
  }

  /**
   * Optimize for the case where {@link #obtain()} is called many times and
   * condition checks effect performance.
   */
  private Provider<T> createLazyInitProvider(final int capacity, final Pool<T> innerPool,
      final Provider<T> elementCreator, final Runnable fullyInitializedListener) {
    return new Provider<T>(){
      int creationCount = 0;

      @Override public T get() {
        synchronized (innerPool) {
          if (innerPool.usableCount() > 0) {
            return innerPool.obtain();
          }
          
          if (++creationCount >= capacity) {
            // switch back to just using the innerPool
            LazyInitPool.this.provider = new Provider<T>() {
              @Override public T get() {
                return innerPool.obtain();
              }
            };
  
            if (fullyInitializedListener != null) {
              fullyInitializedListener.run();
            }
          }
          return elementCreator.get();
        }
      }
    };
  }

  @Override
  public T obtain() {
    return provider.get();
  }

  @Override
  public boolean recycle(T element) {
    return innerPool.recycle(element);
  }
  
  @Override
  public int usableCount() {
    return innerPool.usableCount();
  }
}
