package runner;

import java.util.HashSet;
import java.util.Set;

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
	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	
	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	public DataInitalizer(UserRepo userRepo, RoleRepo roleRepo) {
		super();
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
	}

	@Override
	public void run(String... args) throws Exception {
		
		Long roleCount = roleRepo.count();
		Long userCount = userRepo.count();
		
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
			admin.setPassword(this.encoder.encode("pass123"));
			admin.setVerified(true);
			admin.setRoles(roles);
			
			users.add(admin);
			this.userRepo.saveAll(users);
		}
		
		System.out.println("Data initialized");
		
	}
	
	
	
}
