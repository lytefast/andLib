package com.andLib.pool.impl;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class BlockingPoolTest {
  private static final int CAPACITY = 2;
  private BlockingPool<Object> pool;

  @Before
  public void setUp() throws Exception {
    pool = new BlockingPool<>(CAPACITY);
  }

  @Test
  public void obtain_blocks() throws InterruptedException, ExecutionException {
    ListeningExecutorService listeningExecutorService =
        MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(CAPACITY));
    final AtomicBoolean started = new AtomicBoolean(false);
    ListenableFuture<Object> submit = listeningExecutorService.submit(new Callable<Object>() {
      @Override public Object call() throws Exception {
        started.set(true);
        return pool.obtain();
      }
    });
    while (!started.get()) {
      Thread.yield();
    }
    Thread.yield(); // just to be safe
    assertFalse("call should be blocked", submit.isDone());

    final Object cached = new Object();
    listeningExecutorService.submit(new Runnable() {
      @Override public void run() {
        pool.recycle(cached);
      }
    }).get();  // Wait for completion

    assertEquals("return value should be the same", cached, submit.get());
  }
}
