package repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entities.UrlMapping;
import entities.Users;

public interface UrlMappingRepo extends JpaRepository<UrlMapping, Long> {
	UrlMapping findByShortUrl(String shortUrl);
	List<UrlMapping> findByUser(Users user);
	boolean existsByShortUrl(String shortenUrl);
}
