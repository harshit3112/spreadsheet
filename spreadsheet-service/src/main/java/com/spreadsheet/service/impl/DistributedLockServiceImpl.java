package com.spreadsheet.service.impl;

import com.spreadsheet.service.DistributedLockService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@Slf4j
public class DistributedLockServiceImpl implements DistributedLockService {

    @Autowired
    private RedissonClient redissonClient;

    private static final long DEFAULT_WAIT_TIME = 10L;
    private static final long DEFAULT_LEASE_TIME = 30L;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    @Override
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, Supplier<T> task) throws Exception {
        RLock lock = redissonClient.getLock(lockKey);
        
        log.debug("Attempting to acquire lock: {}", lockKey);
        
        boolean lockAcquired = false;
        try {
            lockAcquired = lock.tryLock(waitTime, leaseTime, timeUnit);
            
            if (!lockAcquired) {
                log.warn("Failed to acquire lock: {} within {} {}", lockKey, waitTime, timeUnit);
                throw new RuntimeException("Unable to acquire lock for key: " + lockKey + " within timeout");
            }
            
            log.debug("Successfully acquired lock: {}", lockKey);
            return task.get();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting for lock: {}", lockKey, e);
            throw new RuntimeException("Thread interrupted while acquiring lock: " + lockKey, e);
        } catch (Exception e) {
            log.error("Error executing task with lock: {}", lockKey, e);
            throw e;
        } finally {
            if (lockAcquired && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                    log.debug("Successfully released lock: {}", lockKey);
                } catch (Exception e) {
                    log.error("Error releasing lock: {}", lockKey, e);
                }
            }
        }
    }

    @Override
    public <T> T executeWithLock(String lockKey, Supplier<T> task) throws Exception {
        return executeWithLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, DEFAULT_TIME_UNIT, task);
    }
}
