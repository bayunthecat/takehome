package com.example.takehome.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RequestLimitService {

    @Value("${spring.application.request.limit.unauthenticated}")
    private int limitPerUnauthenticated = 5;

    @Value("${spring.application.request.limit.authenticated}")
    private int limitPerAuthenticated = 20;

    @Value("${spring.application.request.limit.time}")
    private int time = 1;

    private final RedisTemplate<String, Long> redis;

    public RequestLimitService(RedisTemplate<String, Long> redis) {
        this.redis = redis;
    }

    public boolean checkRequestsLimitPerUser(String userKey) {
        return checkRequestsLimitPerKey(userKey, limitPerAuthenticated);
    }

    public boolean checkRequestsLimitPerIp(String remoteAddress) {
        return checkRequestsLimitPerKey(remoteAddress, limitPerUnauthenticated);
    }

    private boolean checkRequestsLimitPerKey(String key, int limit) {
        final var requests = redis.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForValue().setIfAbsent(key, 0L, Duration.ofSeconds(time));
                operations.opsForValue().increment(key);
                return operations.exec().stream().skip(1).findFirst().orElse(1);
            }
        });
        if (requests != null && Long.parseLong(requests.toString()) > limit) {
            return true;
        }
        return false;
    }
}
