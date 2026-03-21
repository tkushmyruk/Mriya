package org.example.repository;

import org.example.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Profile findByUserId(Integer userId);

    @Modifying
    @Query("UPDATE Profile p SET p.avatarUrl = :avatarUrl WHERE p.userId = :userId")
    int updateProfileUrlById(Integer userId, String avatarUrl);
}
