package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.dto.AuthenticationRequest;
import org.example.dto.AuthenticationResponse;
import org.example.dto.RegisterRequest;
import org.example.service.PresenceService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final PresenceService presenceService;

    @PostMapping("/auth/register")
    public String register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/auth/authenticate")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) {
        return userService.authenticate(request);
    }

    @GetMapping("/{userId}/online")
    public ResponseEntity<Boolean> isUserOnline(@PathVariable Long userId) {
        return ResponseEntity.ok(presenceService.isUserOnline(userId));
    }

    @PostMapping("/me/heartbeat")
    public ResponseEntity<Void> updateMyStatus(@RequestParam Long userId) {
        presenceService.markAsOnline(userId);
        return ResponseEntity.ok().build();
    }
}
