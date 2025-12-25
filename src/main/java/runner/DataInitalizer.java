package runner;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import entities.Role;
import entities.Users;
import repos.RoleRepo;
import repos.UserRepo;

@Component
public class DataInitalizer implements CommandLineRunner {
	
	@Value("${default.admin.mail}")
	private String defaultAdminMail;
	
	@Value("${default.admin.password}")
	private String defaultAdminPassword;
	
	private static final Logger logger = LoggerFactory.getLogger(DataInitalizer.class);
	
	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	
	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);
	
	public DataInitalizer(UserRepo userRepo, RoleRepo roleRepo) {
		super();
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
	}

	@Override
	public void run(String... args) throws Exception {
		
		/*Long roleCount = roleRepo.count();
		Long userCount = userRepo.count();
		
		if(roleCount == 3 && userCount == 2) {
			return;
		}
		
		Set<Role> roles = new HashSet<Role>();
		Set<Users> users = new HashSet<Users>();
		
		// Create Roles and save them if non present in the dB
		// else find all the roles and set with roles that are present to the Set of Roles
		// to fill the user with the roles

		
		if(roleCount == 0) {
			Role userRole = new Role("USER");
			Role managerRole = new Role("MANAGER");
			Role adminRole = new Role("ADMIN");
			
			roles.add(adminRole);
			roles.add(managerRole);
			roles.add(userRole);
			
			this.roleRepo.saveAll(roles);
		}else {
			// Load roles from existing database
			roles.addAll(this.roleRepo.findAll());
		}
		
		 // create a default user with All the roles
		if(userCount == 0) {

			Users admin = new Users();
			admin.setEmail("vishal@gmail.com");
			admin.setFullName("vishal");
			admin.setPassword(this.encoder.encode("Pass@1239"));
			admin.setVerified(true);
			admin.setRoles(roles);

			users.add(admin);
			this.userRepo.saveAll(users);
		}
		
		System.out.println("Data initialized");*/
		if (defaultAdminPassword == null || defaultAdminPassword.isBlank()) {
		    throw new IllegalStateException("ADMIN PASSWORD must be set in environment");
		}

		initRoles();
		initUsers();
		
	}

	private void initUsers() {
		
		if(!userRepo.existsByEmail("demo@gmail.com")) {
			Users demoUser = new Users();
			demoUser.setFullName("DemoUser");
			demoUser.setEmail("demo@gmail.com");
			demoUser.setPassword(this.encoder.encode("demo123"));
			demoUser.setVerified(true);
			demoUser.setRoles(createManager());
			userRepo.save(demoUser);
			logger.info("Demo User initialized");
			System.out.println("Demo User initialized");
		}else {
			System.out.println("Demo User Already Exists");
		}
		

		if(!userRepo.existsByEmail(defaultAdminMail)) {
			Users admin = new Users();
			admin.setEmail(defaultAdminMail);
			admin.setFullName("Vishal");
			admin.setPassword(this.encoder.encode(defaultAdminPassword));
			admin.setVerified(true);
			admin.setRoles(createAdmin());
			userRepo.save(admin);
			logger.info("Main Admin initialized");
			System.out.println("Main Admin initialized");
		}else {
			System.out.println("Admin User Already Exists");
		}
	
	}

	// IMP: Roles could be Configured for Hierarchy as well but for now we are going with this design;
	private void initRoles() {
		
		long roleCount = roleRepo.count();
		Set<Role> roles = new HashSet<Role>();
		
		if(roleCount == 0) {
			Role userRole = new Role("USER");
			Role managerRole = new Role("MANAGER");
			Role adminRole = new Role("ADMIN");
			
			roles.add(userRole);
			roles.add(managerRole);
			roles.add(adminRole);
			
			roleRepo.saveAll(roles);
			logger.info("Roles Initilazed");
			System.out.println("Roles Initilazed");
		}else {
			logger.info("Roles Already Exists in dB with count of: {}", roleCount);
			System.out.println("Roles Already Exists in dB with count of:" + roleCount);
		}
	}
	
	private Set<Role> createAdmin(){
		// ADMIN has all the roles
		Set<Role> roles = new HashSet<Role>(roleRepo.findAll());
		return roles;
	}
	
	
	private Set<Role> createManager(){
		// Manager has all the roles except the Admin Role, 
		// and Manager has access to all the end-points except for two restricted APIs that only ADMIN can access
		Set<String> roleNames = Set.of("USER", "MANAGER");
		
		return roleRepo.findByRoleIn(roleNames);
		
		
	}
	
	
	
}
