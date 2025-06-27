package shorter.urlshortner.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String username) {
        return buckets.computeIfAbsent(username, this::newBucket);
    }

    private Bucket newBucket(String username) {
        Refill refill = Refill.greedy(5, Duration.ofMinutes(1)); // 5 requests per minute
        Bandwidth limit = Bandwidth.classic(5, refill);
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
}
