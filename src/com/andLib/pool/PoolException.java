package com.andLib.pool;

public class PoolException extends RuntimeException {
  public PoolException() {
    super();
  }

  public PoolException(String msg) {
    super(msg);
  }

  public PoolException(String msg, Throwable t) {
    super(msg, t);
  }

  public PoolException(Throwable t) {
    super(t);
  }
}
