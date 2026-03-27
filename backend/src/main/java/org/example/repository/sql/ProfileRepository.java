package org.example.repository.sql;

import org.example.domain.sql.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findById(Integer userId);

    @Modifying
    @Query("UPDATE Profile p SET p.profilePhoto = :profilePhoto WHERE p.id = :id")
    int updateProfileUrlById(Integer id, String profilePhoto);

    @Query("SELECT p FROM Profile p WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Profile> searchProfiles(@Param("query") String query);
}
