package org.server;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GrowPolicy implements RejectedExecutionHandler {

    private final Lock lock = new ReentrantLock();

    @Override
    public void rejectedExecution(Runnable command,ThreadPoolExecutor executor) {
        lock.lock();
        try {
            executor.setMaximumPoolSize(executor.getMaximumPoolSize() + 1);
        } finally {
            lock.unlock();
        }
        executor.submit(command);
    }
}
