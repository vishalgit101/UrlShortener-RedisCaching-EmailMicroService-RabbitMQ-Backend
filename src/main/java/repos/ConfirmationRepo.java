package repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entities.Confirmation;
import entities.Users;

@Repository
public interface ConfirmationRepo extends JpaRepository<Confirmation, Long> {
	Optional<Confirmation> findByToken(String token);

	Optional<Confirmation> findByUser(Users existingUser);
}
