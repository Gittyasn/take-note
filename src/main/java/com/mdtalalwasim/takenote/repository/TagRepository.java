package com.mdtalalwasim.takenote.repository;

import com.mdtalalwasim.takenote.entity.Tag;
import com.mdtalalwasim.takenote.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    /**
     * Bug #15 fix: Find tag by name scoped to a specific user (not globally).
     * This replaces findByName(String) to prevent cross-user tag sharing.
     */
    Optional<Tag> findByNameAndUser(String name, User user);

    /**
     * Returns all distinct tag names that belong to notes owned by the given user.
     * Only shows tags relevant to this user's notes.
     */
    @Query("SELECT DISTINCT t.name FROM Notes n JOIN n.tags t WHERE n.user = :user")
    List<String> findAllTagNamesByUser(@Param("user") User user);
}
