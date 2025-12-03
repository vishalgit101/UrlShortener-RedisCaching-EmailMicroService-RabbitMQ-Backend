package repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entities.Users;

@Repository
public interface UserRepo extends JpaRepository<Users, Long>{
	Optional<Users> findUserByEmail(String email);

	boolean existsByEmail(String email);
}
