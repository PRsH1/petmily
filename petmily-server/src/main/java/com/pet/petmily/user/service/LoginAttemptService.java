package com.pet.petmily.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    private final ConcurrentHashMap<String, AttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String email) {
        attemptsCache.remove(email);
    }

    public void loginFailed(String email) {
        AttemptInfo info = attemptsCache.getOrDefault(email, new AttemptInfo());
        info.increment();
        attemptsCache.put(email, info);
        log.info("로그인 실패 누적: {} ({}/{}회)", email, info.getCount(), MAX_ATTEMPTS);
    }

    public boolean isBlocked(String email) {
        AttemptInfo info = attemptsCache.get(email);
        if (info == null) return false;
        if (info.isExpired(LOCK_DURATION_MINUTES)) {
            attemptsCache.remove(email);
            return false;
        }
        return info.getCount() >= MAX_ATTEMPTS;
    }

    public long getRemainingLockMinutes(String email) {
        AttemptInfo info = attemptsCache.get(email);
        if (info == null) return 0;
        return LOCK_DURATION_MINUTES - java.time.Duration.between(info.getLastAttemptTime(), LocalDateTime.now()).toMinutes();
    }

    private static class AttemptInfo {
        private int count = 0;
        private LocalDateTime lastAttemptTime = LocalDateTime.now();

        void increment() {
            this.count++;
            this.lastAttemptTime = LocalDateTime.now();
        }

        int getCount() { return count; }

        LocalDateTime getLastAttemptTime() { return lastAttemptTime; }

        boolean isExpired(long lockDurationMinutes) {
            return java.time.Duration.between(lastAttemptTime, LocalDateTime.now()).toMinutes() >= lockDurationMinutes;
        }
    }
}
