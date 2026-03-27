package org.example.repository.sql;


import jakarta.persistence.criteria.CriteriaBuilder;
import org.example.domain.sql.Friendship;
import org.example.domain.sql.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f FROM Friendship f " +
            "WHERE (CAST(f.requester.id AS integer) = :u1 AND CAST(f.addressee.id AS integer) = :u2) " +
            "OR (CAST(f.requester.id AS integer) = :u2 AND CAST(f.addressee.id AS integer) = :u1)")
    Optional<Friendship> findRelation(@Param("u1") Integer u1, @Param("u2") Integer u2);

    @Query("SELECT a FROM Friendship f JOIN f.addressee a JOIN FETCH a.profile WHERE f.requester.id = :id AND f.status = 'ACCEPTED'")
    List<User> findFriendsAsRequester(@Param("id") Integer id);

    @Query("SELECT r FROM Friendship f JOIN f.requester r JOIN FETCH r.profile WHERE f.addressee.id = :id AND f.status = 'ACCEPTED'")
    List<User> findFriendsAsAddressee(@Param("id") Integer id);

    default List<User> findAllAcceptedFriends(Integer userId) {
        List<User> friends = new java.util.ArrayList<>();
        friends.addAll(findFriendsAsRequester(userId));
        friends.addAll(findFriendsAsAddressee(userId));
        return friends;
    }

    @Query("SELECT f.requester FROM Friendship f WHERE f.addressee.id = :id AND f.status = 'PENDING'")
    List<User> findIncomingRequests(@Param("id") Integer userId);

    Optional<Friendship> findByRequesterIdAndAddresseeId(Integer requesterId, Integer addresseeId);
}