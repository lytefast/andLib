package com.andLib.pool.impl.decorators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;

import com.andLib.pool.impl.BasicPool;

public class CleaningPoolTest {
  private static final int  CAPACITY = 2;
  private BasicPool<TestPoolable> innerPoolSpy;
  private CleaningPool<TestPoolable> pool;

  class TestPoolable implements CleaningPool.Poolable{
    boolean isDirty = true;
    @Override public void clean() {
      isDirty = false;
    }
  }

  @Before
  public void setUp() throws Exception {
    innerPoolSpy = spy(new BasicPool<TestPoolable>(CAPACITY));
    pool = new CleaningPool<>(innerPoolSpy);
  }

  @Test
  public void recycle_Cleans() {
    final TestPoolable poolable = new TestPoolable();
    assertTrue("dirty to start", poolable.isDirty);
    pool.recycle(poolable);
    assertFalse("cleaned", poolable.isDirty);
  }

}
