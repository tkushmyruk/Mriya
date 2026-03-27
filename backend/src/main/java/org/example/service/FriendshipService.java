package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.domain.sql.Friendship;
import org.example.domain.sql.FriendshipStatus;
import org.example.domain.sql.User;
import org.example.repository.sql.FriendshipRepository;
import org.example.repository.sql.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendFriendRequest(int requesterId, int addresseeId) {
        if (requesterId == addresseeId) {
            throw new IllegalArgumentException("Ви не можете додати самого себе у друзі");
        }

        Optional<Friendship> existing = friendshipRepository.findRelation(requesterId, addresseeId);

        if (existing.isPresent()) {
            Friendship relation = existing.get();

            if (relation.getStatus() == FriendshipStatus.PENDING) {
                if (relation.getRequester().getId().equals(requesterId)) return;

                relation.setStatus(FriendshipStatus.ACCEPTED);
                return;
            }
            return;
        }

        User requester = userRepository.getReferenceById(requesterId);
        User addressee = userRepository.getReferenceById(addresseeId);

        Friendship newFriendship = new Friendship();
        newFriendship.setRequester(requester);
        newFriendship.setAddressee(addressee);
        newFriendship.setStatus(FriendshipStatus.PENDING);

        friendshipRepository.save(newFriendship);
    }

    @Transactional
    public void acceptFriendRequest(Integer userId, Integer requesterId) {
        Friendship friendship = friendshipRepository.findByRequesterIdAndAddresseeId(requesterId, userId)
                .orElseThrow(() -> new RuntimeException("Запит не знайдено"));

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            return;
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
    }

    @Transactional
    public void declineFriendRequest(Integer userId, Integer otherUserId) {
        friendshipRepository.findRelation(userId, otherUserId)
                .ifPresent(friendshipRepository::delete);
    }
}
