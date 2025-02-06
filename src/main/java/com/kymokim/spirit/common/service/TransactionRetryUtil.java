package com.kymokim.spirit.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DeadlockLoserDataAccessException;

import java.util.function.Supplier;

public class TransactionRetryUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionRetryUtil.class);

    public static <T> T executeWithRetry(Supplier<T> function, int retryCount) {
        for (int i = 0; i < retryCount; i++) {
            try {
                return function.get();
            } catch (DeadlockLoserDataAccessException e) {
                LOGGER.warn("Deadlock detected, retrying... Attempt {}/{}", i + 1, retryCount);
                if (i == retryCount - 1) throw e;
                try {
                    Thread.sleep(100); // 100ms 대기 후 재시도
                } catch (InterruptedException ignored) {
                }
            }
        }
        throw new IllegalStateException("Should not reach here");
    }
}