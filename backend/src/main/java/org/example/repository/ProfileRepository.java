package org.example.repository;

import org.example.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findById(Integer userId);

    @Modifying
    @Query("UPDATE Profile p SET p.profilePhoto = :profilePhoto WHERE p.id = :id")
    int updateProfileUrlById(Integer id, String profilePhoto);
}
