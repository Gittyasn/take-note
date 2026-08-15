package com.mdtalalwasim.takenote.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mdtalalwasim.takenote.entity.Notes;
import com.mdtalalwasim.takenote.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotesRepository extends JpaRepository<Notes, Integer> {
	
	List<Notes> findByUser(User user);

	Page<Notes> findByUser(User user, Pageable pageable);
	
	Page<Notes> findByUserAndFavoriteTrue(User user, Pageable pageable);

	Page<Notes> findByUserAndTagsName(User user, String tagName, Pageable pageable);

	@Query("SELECT n FROM Notes n WHERE n.user = :user AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(n.description) LIKE LOWER(CONCAT('%', :query, '%')))")
	Page<Notes> searchNotesByUser(@Param("user") User user, @Param("query") String query, Pageable pageable);

	long countByUser(User user);

	long countByUserAndFavoriteTrue(User user);
}
