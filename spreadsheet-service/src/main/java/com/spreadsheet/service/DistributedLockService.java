package com.spreadsheet.service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface DistributedLockService {
    
    /**
     * Execute a task with distributed lock
     * @param lockKey The key for the lock
     * @param waitTime Maximum time to wait for the lock
     * @param leaseTime Maximum time to hold the lock
     * @param timeUnit Time unit for waitTime and leaseTime
     * @param task The task to execute
     * @return Result of the task execution
     * @throws Exception if lock cannot be acquired or task execution fails
     */
    <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, Supplier<T> task) throws Exception;
    
    /**
     * Execute a task with distributed lock using default timeout values
     * @param lockKey The key for the lock
     * @param task The task to execute
     * @return Result of the task execution
     * @throws Exception if lock cannot be acquired or task execution fails
     */
    <T> T executeWithLock(String lockKey, Supplier<T> task) throws Exception;
}
