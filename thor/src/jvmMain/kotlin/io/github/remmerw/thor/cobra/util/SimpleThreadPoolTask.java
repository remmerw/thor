package io.github.remmerw.thor.cobra.util;

public interface SimpleThreadPoolTask extends Runnable {
    void cancel();
}
