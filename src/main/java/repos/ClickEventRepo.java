package repos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import entities.ClickEvent;
import entities.UrlMapping;

@Repository
public interface ClickEventRepo extends JpaRepository<ClickEvent, Long>{
	

	@Query(""" 
			SELECT ce
			from ClickEvent ce
			where ce.urlMapping = :urlMapping
			AND ce.clickDate between :start AND :end
			""")
	List<ClickEvent> findClicksByUrlMappingAndDateRange(
			@Param("urlMapping") UrlMapping urlMapping,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end
			);
	
	@Query("""
			Select ce 
			from ClickEvent ce
			where ce.urlMapping IN :urlMappings
			AND ce.clickDate BETWEEN :start AND :end
			""")
	List<ClickEvent> findByUrlMappingsInAndClickDateBetween(@Param("urlMappings") List<UrlMapping> urlMappings, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


	Page<ClickEvent> findClickEventsByUrlMapping(UrlMapping urlMapping, Pageable pageable);
	
	
}
