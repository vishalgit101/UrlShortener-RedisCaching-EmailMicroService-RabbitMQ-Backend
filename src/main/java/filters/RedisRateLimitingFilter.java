package filters;

import java.io.IOException;
import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RedisRateLimitingFilter implements Filter {
	private StringRedisTemplate redisTemplate;
	
	private static final int LIMIT = 10;
	private static final int WINDOW_DURATION = 60;
	
	public RedisRateLimitingFilter(StringRedisTemplate redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// LIMITS THE NUMBER OF REQUEST FROM A CERTAIN IP TO 10 IN 60sec
		// REDIS STORES THE IP -> with Request Counter in a minute
		// and expires the IP key after a minute
		// exipiry is set when first request is made, on subsequenst request count for the ip is incremented
		// and expiry remains the same
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		String clientIp =  httpRequest.getRemoteAddr(); // will give use the remote ip address of the user
		String key = "rate_limit:" + clientIp;
		Long requestCount = redisTemplate.opsForValue().increment(key,1);
		
		if(requestCount == 1) { // initalize the expiry
			redisTemplate.expire(key, Duration.ofSeconds(WINDOW_DURATION));
		}
		
		if(requestCount > LIMIT) {
			httpResponse.setStatus(429);
			httpResponse.setContentType("application/json");
			httpResponse.getWriter().write("""
				    { "error": "Too Many Requests",
				      "message": "Rate limit exceeded. Try again later." }
				""");
			httpResponse.getWriter().flush();
			return; // ends the request and returns the response from here
		}
		// if all good and no limit exceeded, the request should procede
		chain.doFilter(request, response);
	}
	
	
	
}
