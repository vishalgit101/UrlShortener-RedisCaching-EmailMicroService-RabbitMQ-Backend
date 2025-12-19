package configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import filters.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import services.MyUserDetailService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableAsync
public class SecurityConfigs {
	// DI and Class variables comes here
	private final UserDetailsService myUserDetailService;
	private final JwtFilter jwtFilter;
	
	public SecurityConfigs(UserDetailsService myUserDetailService, JwtFilter jwtFilter) {
		super();
		this.myUserDetailService = myUserDetailService;
		this.jwtFilter = jwtFilter;
	}


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(customizer -> customizer.disable());
		
		http.authorizeHttpRequests(request -> 
			request.requestMatchers("/api/auth/public/**").permitAll()
			.requestMatchers("/{shortUrl}").permitAll()
			.requestMatchers("/api/admin").hasRole("ADMIN")
			.anyRequest().authenticated());
		
		//http.formLogin(Customizer.withDefaults());
		http.httpBasic(Customizer.withDefaults());
		
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		// add jwt filter here before the userpassword filter
		
		http.exceptionHandling(exception -> exception

			    // 401 → not authenticated (missing / invalid JWT)
			    .authenticationEntryPoint((request, response, authException) -> {
			        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			        response.setContentType("application/json");
			        response.getWriter().write(
			            "{\"error\": \"Unauthorized\"}"
			        );
			    })

			    // 403 → authenticated but role/authority mismatch
			    .accessDeniedHandler((request, response, accessDeniedException) -> {
			        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			        response.setContentType("application/json");
			        response.getWriter().write(
			            "{\"error\": \"Access denied: insufficient permissions\"}"
			        );
			    })
			);



		
		return http.build();
	}
	
	
	// We can add Inmermory authentication here like this:
	/*@Bean
	public UserDetailsService userDetailsService() {
		UserDetails vishal = User.builder()
				.username("Vishal")
				.password("{noop}pass123")
				.roles("ADMIN", "USER")
				.build();
		
		UserDetails harjeet = User.builder()
				.username("Harjeet")
				.password("{noop}pass125")
				.roles("ADMIN", "MANAGER", "USER")
				.build();
		
		return new InMemoryUserDetailsManager(vishal, harjeet);
	}*/
	
	// now create Auth Provider, DAO provider in our case, for storing the users and password in the database
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(this.myUserDetailService); // add the custom dependency of userDeatilService here inside the constructor
		provider.setPasswordEncoder(new BCryptPasswordEncoder(8));
		//provider.setUserDetailsService(null);
		return provider;
	}
	
	// another bean for auth manager
	@Bean
	public AuthenticationManager authManger(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	
  /*@Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
            ROLE_ADMIN > ROLE_MANAGER
            ROLE_MANAGER > ROLE_USER
        """);
    }*/
	
	// The Above Snippit defines the role hiearchy in SPring Security, with that we could avoid assigning mutiple related
	// roles in dB that implies the hiearchy and thus update authority could be very simplified
	// will refactor later for now we keep the default flow to quickly finish the project
	
}
