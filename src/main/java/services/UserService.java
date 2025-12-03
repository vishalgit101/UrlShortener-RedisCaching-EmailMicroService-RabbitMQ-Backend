package services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dtos.UserLoginDTO;
import dtos.UserRegisterDTO;
import entities.Confirmation;
import entities.Role;
import entities.Users;
import exceptions.UserAlreadyExistsException;
import repos.ConfirmationRepo;
import repos.RoleRepo;
import repos.UserRepo;

@Service
public class UserService {
	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	private final JwtService jwtService;
	private final AuthenticationManager authManger;
	private final EmailService emailService;
	private final ConfirmationRepo confirmationRepo;
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	
	public UserService(UserRepo userRepo, RoleRepo roleRepo, JwtService jwtService, 
			AuthenticationManager authManger, EmailService emailService, ConfirmationRepo confirmationRepo) {
		super();
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.jwtService = jwtService;
		this.authManger = authManger;
		this.emailService = emailService;
		this.confirmationRepo = confirmationRepo;
	}
	
	public Users registerUser(UserRegisterDTO dto) {
		
		// first check if the user already exits
		Users existingUser = this.userRepo.findUserByEmail(dto.getEmail().trim().toLowerCase()).orElse(null);
		
		if(existingUser != null) {
			if(existingUser.isVerified()) {
				logger.info("User exists");
				throw new UserAlreadyExistsException("User with: " + dto.getEmail() +  " already exists");
			}else {
	            logger.info("User exists but not verified");
	            // place holder trigger resend verification email here and exit the method
	            
	            // update the older confirmation for the existing user
	            Confirmation existingConfimation = confirmationRepo.findByUser(existingUser).orElseThrow(()-> new RuntimeException("Confirmation dont exists"));
	            
	            // if its older than 24 hr refresh the token and send the email if not then tell the user to check inbox
	            
	            boolean val = existingConfimation.getCreated().isAfter(LocalDateTime.now().minusHours(24));
	            
	            if(val) {
	            	throw new RuntimeException("Please check your inbox for verification code");
	            }
	            
	            existingConfimation.setToken(UUID.randomUUID().toString());
	            existingConfimation.setCreated(LocalDateTime.now());
	            this.confirmationRepo.save(existingConfimation); // updates it for the existing user
	            
	            this.emailService.sendSimpleMailMessage(existingUser.getFullName(), existingUser.getEmail(), existingConfimation.getToken());
	            // delete the previous token and send the new one
	            
	            return existingUser;
	            /*throw new RuntimeException(
	                "A registration attempt already exists for this email. Please verify your email to continue."
	            );*/
			}
		}
		
		// if existsing user is null then the follwoing block will exceute
		Users user = new Users();
		user.setVerified(false);
		user.setFullName(dto.getFullName());
		user.setPassword(this.encoder.encode(dto.getPassword()));
		user.setEmail(dto.getEmail().toLowerCase().trim());
		// get the role to save the user with the default role
		Role role = this.roleRepo.findByRole("USER").orElseThrow(()-> new RuntimeException("Role Not found"));
		user.getRoles().add(role);
		role.getUsers().add(user);
		this.userRepo.save(user);
		
		Confirmation confirmation = new Confirmation(user);
		this.confirmationRepo.save(confirmation);
		
		// now send the email
		this.emailService.sendSimpleMailMessage(user.getFullName(), user.getEmail(), confirmation.getToken());
		return user;
	}
	
	public  boolean userConfirmation(String tok) {
		// this service metho confirms the user by checking the confirmation
		// user is fetched as well by the confirmation because of one to one mapping
		// enable that particular user save him in he repo and delte the token, also check for the expiry
		// should be valid till 24hr
		
		// VERY IMP
		// We can carete custom enum VerificationStatus and return that
		Confirmation confirmation = this.confirmationRepo.findByToken(tok).orElse(null);
		if(confirmation == null) {
			return false;
		}
		
		// prevent double verification
		if (confirmation.getUser().isVerified()) {
		    this.confirmationRepo.delete(confirmation);
		    return true;
		}

		
		boolean isValid = confirmation.getCreated().isAfter(LocalDateTime.now().minusHours(24));
			
		if(!isValid) {
			// place holder exception, handle it with custom exception by sending httpResonse
			// or we can make verification token to live on forever
			this.confirmationRepo.delete(confirmation);
			//throw new RuntimeException("Verification token expired! Try creating your account again");
			return false;
		}
		
		// if valid
		Users user = confirmation.getUser();
		
		user.setVerified(true);
		
		this.userRepo.save(user);
		this.confirmationRepo.delete(confirmation);
		
		return true;
	}
	
	
	public String userLoginVerification(UserLoginDTO userLoginDTO) {
		
		Users user = this.userRepo.findUserByEmail(userLoginDTO.getEmail().trim().toLowerCase()).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
		
		if(!user.isVerified()) {
			throw new RuntimeException("User is not verified. " + user.getFullName() + ", please verify you account before proceeding");
		}
		
		Authentication authentication = this.authManger.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword())); 
		
		if(authentication.isAuthenticated()) {
			// if successfully authenticated then generate the jwt token and send it to the frontend or store in http cookies
			return this.jwtService.generateToken(userLoginDTO.getEmail());
		}
		
		// else throw the exception !
		throw new RuntimeException("Login Failed!");
	}
	
	public Users findUserByEmail(String email) {
		// later move the exception in the global exception handling
		return this.userRepo.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found"));
	}
	
	
}
