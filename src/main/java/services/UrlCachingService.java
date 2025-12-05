package services;

import java.time.Duration;

import org.jboss.logging.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import entities.UrlMapping;
import repos.UrlMappingRepo;


@Service
public class UrlCachingService {
	private final RedisTemplate<String, Object> redisTemplate;
	private final UrlMappingRepo urlMappingRepo;
	private static final String URL_KEY = "url::";
	private final Logger logger = Logger.getLogger(UrlCachingService.class);

	public UrlCachingService(RedisTemplate<String, Object> redisTemplate,  UrlMappingRepo urlMappingRepo) {
		super();
		this.redisTemplate = redisTemplate;
		this.urlMappingRepo = urlMappingRepo;
	}
	
	public void saveUrl(String shortUrl, String originalUrl) {
		redisTemplate.opsForValue().set(URL_KEY + shortUrl, originalUrl, Duration.ofDays(10));
	}
	
	public String getOriginal(String shortUrl) {
		String cachedOriginal =  (String) redisTemplate.opsForValue().get(URL_KEY + shortUrl);
		
		if(cachedOriginal != null) {
			redisTemplate.expire(URL_KEY + shortUrl, Duration.ofDays(10));
			logger.info("Got the url from Redis Cached memory");
			return cachedOriginal;
		}
		
		// if cached is null
		UrlMapping urlMapping = this.urlMappingRepo.findByShortUrl(shortUrl);
		
		if(urlMapping == null) {
			//throw new RuntimeException("No such short url: " + shortUrl + " exists in the database");
			return null; // so that controller handles it
		}
		
		// if not null, save it to the cache
		redisTemplate.opsForValue().set(URL_KEY + shortUrl, urlMapping.getOriginalUrl(), Duration.ofDays(10));
		logger.info("Got the url from Redis dB Query");
		return urlMapping.getOriginalUrl();
	}
	
	public void deleteUrlCaching(String shortUrl) {
		redisTemplate.delete(URL_KEY + shortUrl);
	}
	
}
