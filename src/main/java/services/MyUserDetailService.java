package services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import entities.Users;
import model.UserPrincipal;
import repos.UserRepo;

@Service
public class MyUserDetailService implements UserDetailsService {
	// DI 
	private final UserRepo userRepo;

	public MyUserDetailService(UserRepo userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Fetch the user with the help of username or email and return the that user object modifed with the userdetails so that spring securitry can pass it down the filter chain and compare it against the useranme passowrd from the login side
		Users user = this.userRepo.findUserByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User with email: " + username +  " not found"));
		
		UserPrincipal principal = new UserPrincipal(user);
		
		return principal;
	}

}
