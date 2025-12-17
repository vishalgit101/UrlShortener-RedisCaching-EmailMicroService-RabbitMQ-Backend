package services;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dtos.ClickEventDto;
import dtos.UrlMappingDto;
import entities.ClickEvent;
import entities.UrlMapping;
import entities.Users;
import exceptions.ResourceNotFoundException;
import repos.ClickEventRepo;
import repos.UrlMappingRepo;

@Service
public class UrlMappingService {
	
	private final UserService userService;
	private final UrlMappingRepo urlMappingRepo;
	private final ClickEventRepo clickEventRepo;
	private final UrlCachingService urlCachingService;
	
	private Logger logger = LoggerFactory.getLogger(UrlMappingService.class);
	
	private static final SecureRandom random = new SecureRandom();
	private static final String CHARACTERS =
	        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int SHORT_LENGTH = 6;
	
	public UrlMappingService(UserService userService, UrlMappingRepo urlMappingRepo, ClickEventRepo clickEventRepo, 
			UrlCachingService urlCachingService) {
		super();
		this.userService = userService;
		this.urlMappingRepo = urlMappingRepo;
		this.clickEventRepo = clickEventRepo;
		this.urlCachingService = urlCachingService;
	}
	
	public UrlMapping findByShortUrl(String shortUrl) {
		return this.urlMappingRepo.findByShortUrl(shortUrl);
	}

	public UrlMappingDto createShortUrl(String originalUrl, Users user) {
		/*String shortenUrl = generateShortUrl();
		// check if the shortenUrl doesn't exits already
		boolean exists = this.urlMappingRepo.existsByShortUrl(shortenUrl);
		
		while(exists) {
			shortenUrl = generateShortUrl();
			exists = this.urlMappingRepo.existsByShortUrl(shortenUrl);
		}*/
		
	    String shortUrl;
	    do {
	        shortUrl = generateShortUrl();
	    } while (urlMappingRepo.existsByShortUrl(shortUrl));
	    
		UrlMapping urlMapping = new UrlMapping();
		urlMapping.setClickCount(0);
		urlMapping.setCreateDate(LocalDateTime.now());
		urlMapping.setShortUrl(shortUrl);
		urlMapping.setOriginalUrl(originalUrl);
		urlMapping.setUser(user);
		UrlMapping savedUrlMapping = this.urlMappingRepo.save(urlMapping);
		
	    UrlMappingDto dto =  convertToDto(savedUrlMapping);
	    this.urlCachingService.saveUrl(shortUrl, originalUrl);
		return dto;
	}
	
	private UrlMappingDto convertToDto(UrlMapping savedUrlMapping) {
		UrlMappingDto urlMappingDto = new UrlMappingDto();
		urlMappingDto.setClickCount(savedUrlMapping.getClickCount());
		urlMappingDto.setCreateDate(savedUrlMapping.getCreateDate());
		urlMappingDto.setId(savedUrlMapping.getId());
		urlMappingDto.setOriginalUrl(savedUrlMapping.getOriginalUrl());
		urlMappingDto.setShortUrl(savedUrlMapping.getShortUrl());
		urlMappingDto.setUsername(savedUrlMapping.getUser().getEmail());
		
		return urlMappingDto;
		
	}

	/*public String generateShortUrl(String originalUrl) {
		Random random = new Random();
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder shortUrl = new StringBuilder(6); // with capacity
		
		// could modify the code as well to avoid the collision of the keys
		for(int i = 0; i < 6; i++) {
			shortUrl.append(characters.charAt(random.nextInt(characters.length())));
		}
		
		return shortUrl.toString();
		
	}*/
	
	private String generateShortUrl() {
	    StringBuilder sb = new StringBuilder(SHORT_LENGTH);
	    for (int i = 0; i < SHORT_LENGTH; i++) {
	        sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
	    }
	    return sb.toString();
	}

	public List<UrlMappingDto> getUrlsByUser(Users user) {
		List<UrlMapping> urlMappings =  this.urlMappingRepo.findByUser(user);
		List<UrlMappingDto> urlMappingDtos = new ArrayList<UrlMappingDto>();
		for(UrlMapping urlObj : urlMappings) {
			urlMappingDtos.add(convertToDto(urlObj));
		}
		return urlMappingDtos;
	}
	
	public Page<UrlMappingDto> getUrlsByUser(Users user, int page, int size){
		
		Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());
		Page<UrlMapping> urlPage = urlMappingRepo.findByUser(user, pageable);
		
		List<UrlMappingDto> urlMappingDtos = new ArrayList<UrlMappingDto>();
		
		for(UrlMapping urlObj : urlPage.getContent()) {
			urlMappingDtos.add(convertToDto(urlObj));
		}
		
		Page<UrlMappingDto> dtoPage = new PageImpl<UrlMappingDto>(urlMappingDtos, pageable, urlPage.getTotalElements());
		
		return dtoPage;
	}

	public List<ClickEventDto> getClickEventsByDate(String shortUrl, LocalDateTime start, LocalDateTime end) {
		UrlMapping urlMapping = this.urlMappingRepo.findByShortUrl(shortUrl);  // single urlMapping is returned for a given shortUrl
		
		if(urlMapping == null) {
			logger.info("No ShortUrl mapiing for: " + shortUrl + " exits");
			System.out.println("No ShortUrl mapiing for: " + shortUrl + " exits");
			return new ArrayList<>(); // return empty list if URL not found or throw no Resource found
		}
		
		logger.info("ShortUrl mapiing for: " + shortUrl + " exits");
		System.out.println("ShortUrl mapiing for: " + shortUrl + " exits");
		// Now Fetch all the click events from this particular URL in the date range
		List<ClickEvent> clicks = this.clickEventRepo.findClicksByUrlMappingAndDateRange(urlMapping, start, end);
		
		if(clicks == null) {
			logger.info("Failed to find the click events");
			throw new ResourceNotFoundException("No click events between the request date found");
		}
		// Group clicks by date and count them
		Map<LocalDate, Integer> clicksByDate = new HashMap<>(); 
		
		for(ClickEvent click: clicks) {
			LocalDate date = click.getClickDate().toLocalDate();
			
			if(clicksByDate.containsKey(date)) { // date here cos we are adding LocalDate and not the LocalDateTime
				clicksByDate.put(date, clicksByDate.get(date) + 1);
			}else {
				clicksByDate.put(date, 1);
			}
		}
		
		// Now convert to the DTO
		List<ClickEventDto> result = convertToClickEventDtos(clicksByDate);
		
		
		return result;
	}
	
	public List<ClickEventDto> convertToClickEventDtos(Map<LocalDate, Integer> clicksByDate) {
		List<ClickEventDto> result = new ArrayList<ClickEventDto>();
		for(Map.Entry<LocalDate, Integer> entry : clicksByDate.entrySet()) {
			ClickEventDto dto = new ClickEventDto();
			dto.setClickDate(entry.getKey());
			dto.setCount(entry.getValue());
			result.add(dto);
		}
		
		return result;
	}

	// this is for to check how many total clicks a users all the urlMappings got on a specific day
	public Map<LocalDate, Integer> getTotalClicksByUserAndDate(Users user, LocalDate start, LocalDate end) {
		List<UrlMapping> urlMappings = this.urlMappingRepo.findByUser(user); // gives all the url mappings corresponding to a particular user
		
		// now, with the help of these urlMappings get the clickEvents of all the urlMappings between a date-range
		List<ClickEvent> clickEvents = this.clickEventRepo.findByUrlMappingsInAndClickDateBetween(urlMappings, start.atStartOfDay(), end.plusDays(1).atStartOfDay());
		
		// now group up all the clickEvents date-wise with count
		Map<LocalDate, Integer> clicksByDate = new HashMap<LocalDate, Integer>();
		
		for(ClickEvent click: clickEvents) {
			LocalDate date = click.getClickDate().toLocalDate();
			
			if(clicksByDate.containsKey(date)) {
				clicksByDate.put(date, clicksByDate.get(date) + 1);
			}else {
				clicksByDate.put(date, 1);
			}
		}
		
		return clicksByDate;
		
	}

	// get the UrlMapping, record the click and ClickEvent and return the urlMapping
	@Async
	@Transactional
	public void recordClickEvent(String shortUrl) {
		
		UrlMapping urlMapping = this.urlMappingRepo.findByShortUrl(shortUrl);
		
		//this.urlMappingRepo.incrementClick(shortUrl); uncomment it to handle race conditions when app grows, for now its fine
		
		if(urlMapping != null) {
			urlMapping.setClickCount(urlMapping.getClickCount() +1); //put/replace this with the commented code above to deal with the race condition, with atomic increment
			urlMappingRepo.save(urlMapping);
			
			// Now record the click Event as well
			ClickEvent clickEvent = new ClickEvent();
			clickEvent.setClickDate(LocalDateTime.now());
			clickEvent.setUrlMapping(urlMapping);
			clickEventRepo.save(clickEvent);
		}else {
			throw new ResourceNotFoundException("No such short url exists in the database");
		}
	}
	
	
	
}
