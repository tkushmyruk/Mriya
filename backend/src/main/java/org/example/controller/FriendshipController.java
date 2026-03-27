package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.sql.User;
import org.example.repository.sql.FriendshipRepository;
import org.example.repository.sql.UserRepository;
import org.example.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @PostMapping("/request/{id}")
    public ResponseEntity<Void> sendRequest( Principal principal, @PathVariable Integer id) {

        Integer userId = getUserByPrincipal(principal).getId();
        friendshipService.sendFriendRequest(userId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<String> getStatus(
            Principal principal,
            @PathVariable Integer id) {
        Integer userId = getUserByPrincipal(principal).getId();

        return friendshipRepository.findRelation(userId, id)
                .map(f -> f.getStatus().toString())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok("NONE"));
    }
    private User getUserByPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/all")
    public List<User> getFriends(Principal principal) {
        Integer userId = getUserByPrincipal(principal).getId();
        return friendshipRepository.findAllAcceptedFriends(userId);
    }

    @GetMapping("/requests")
    public List<User> getRequests(Principal principal) {
        Integer userId = getUserByPrincipal(principal).getId();
        return friendshipRepository.findIncomingRequests(userId);
    }

    @PostMapping("/accept/{requesterId}")
    public ResponseEntity<Void> acceptRequest(Principal principal, @PathVariable Integer requesterId) {
        Integer myId = getUserByPrincipal(principal).getId();
        friendshipService.acceptFriendRequest(myId, requesterId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/decline/{otherId}")
    public ResponseEntity<Void> declineRequest(Principal principal, @PathVariable Integer otherId) {
        Integer myId = getUserByPrincipal(principal).getId();
        friendshipService.declineFriendRequest(myId, otherId);
        return ResponseEntity.ok().build();
    }
}
