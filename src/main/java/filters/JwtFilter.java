package filters;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import entities.Users;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repos.UserRepo;
import services.JwtService;
import services.MyUserDetailService;
import services.UserService;

@Service
public class JwtFilter extends OncePerRequestFilter{
	
	// DI JwtService
	private final JwtService jwtService;
	//private final UserRepo userRepo; will create circular dependency problem so instead use context to get its bean
	private final ApplicationContext context;

	public JwtFilter(JwtService jwtService, ApplicationContext context) {
		super();
		this.jwtService = jwtService;
		this.context = context;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// get the authentication header from the request object
		String authHeader = request.getHeader("Authorization");;
		String email = null;
		String token = null;
		
		if(authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			email = this.jwtService.extractUsername(token);
			
			if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.context.getBean(MyUserDetailService.class).loadUserByUsername(email);
				
				if(this.jwtService.validateToken(userDetails, token)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					
					// pass the request object in the authToken
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					// now put the authToken in the Security Context
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
	
		}
		// forward the request and response object
		filterChain.doFilter(request, response);
	}

}
