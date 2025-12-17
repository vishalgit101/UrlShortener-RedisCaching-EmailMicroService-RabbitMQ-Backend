package repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import entities.UrlMapping;
import entities.Users;

public interface UrlMappingRepo extends JpaRepository<UrlMapping, Long> {
	UrlMapping findByShortUrl(String shortUrl);
	List<UrlMapping> findByUser(Users user);
	boolean existsByShortUrl(String shortenUrl);
	
	Page<UrlMapping> findByUser(Users user, Pageable pageable);
	
	// Atomic SQL Update
	@Modifying
	@Query("UPDATE UrlMapping u SET u.clickCount = u.clickCount + 1 WHERE u.shortUrl = :shortUrl")
	void incrementClick(@Param("shortUrl") String shortUrl);
}
