package services;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class UrlCachingService {
	private final RedisTemplate<String, Object> redisTemplate;
	private static final String URL_KEY = "url::";

	public UrlCachingService(RedisTemplate<String, Object> redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
	}
	
	public void saveUrl(String shortUrl, String originalUrl) {
		redisTemplate.opsForValue().set(URL_KEY + shortUrl, originalUrl, Duration.ofDays(10));
	}
	
	public String getUrlMappingDto(String shortUrl) {
		String cachedOriginal =  (String) redisTemplate.opsForValue().get(URL_KEY + shortUrl);
		
		if(cachedOriginal != null) {
			redisTemplate.expire(URL_KEY + shortUrl, Duration.ofDays(10));
		}
		
		return cachedOriginal;
	}
	
	public void deleteUrlCaching(String shortUrl) {
		redisTemplate.delete(URL_KEY + shortUrl);
	}
	
}
