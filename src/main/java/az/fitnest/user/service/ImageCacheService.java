package az.fitnest.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCacheService {

    private final RedisTemplate<String, byte[]> binaryRedisTemplate;
    private static final String CACHE_PREFIX = "image:cache:";
    private static final long DEFAULT_TTL_HOURS = 24;

    public byte[] getImage(String fsId) {
        try {
            return binaryRedisTemplate.opsForValue().get(CACHE_PREFIX + fsId);
        } catch (Exception e) {
            log.error("Failed to get image from cache for fsId: {}", fsId, e);
            return null;
        }
    }

    public void cacheImage(String fsId, byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        try {
            binaryRedisTemplate.opsForValue().set(CACHE_PREFIX + fsId, data, Duration.ofHours(DEFAULT_TTL_HOURS));
            log.info("Cached image for fsId: {} ({} bytes)", fsId, data.length);
        } catch (Exception e) {
            log.error("Failed to cache image for fsId: {}", fsId, e);
        }
    }
}
