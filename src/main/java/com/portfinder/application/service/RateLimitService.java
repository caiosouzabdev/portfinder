package com.portfinder.application.service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RateLimitService {

	private final Map<String, Deque<Long>> buckets = new ConcurrentHashMap<>();
	private final int votesPerMinute;
	private final int submitsPerHour;

	public RateLimitService(
			@Value("${portfinder.rate-limit.votes-per-minute:30}") int votesPerMinute,
			@Value("${portfinder.rate-limit.submits-per-hour:5}") int submitsPerHour) {
		this.votesPerMinute = votesPerMinute;
		this.submitsPerHour = submitsPerHour;
	}

	public void checkVote(Long userId) {
		check("vote:" + userId, votesPerMinute, 60_000L);
	}

	public void checkSubmit(Long userId) {
		check("submit:" + userId, submitsPerHour, 3_600_000L);
	}

	private void check(String key, int limit, long windowMs) {
		long now = Instant.now().toEpochMilli();
		Deque<Long> times = buckets.computeIfAbsent(key, k -> new ArrayDeque<>());
		synchronized (times) {
			while (!times.isEmpty() && now - times.peekFirst() > windowMs) {
				times.removeFirst();
			}
			if (times.size() >= limit) {
				throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
			}
			times.addLast(now);
		}
	}
}
