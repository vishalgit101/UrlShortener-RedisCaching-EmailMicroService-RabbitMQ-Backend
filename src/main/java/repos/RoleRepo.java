package repos;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entities.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long>{
	Optional<Role> findByRole(String name);

	Set<Role> findByRoleIn(Set<String> roleNames);


}
