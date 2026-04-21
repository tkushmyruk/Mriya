package org.example.controller;

import jakarta.mail.MessagingException;
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
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) throws MessagingException {
        userService.register(request);
        return ResponseEntity.ok(java.util.Map.of("message", "Код надіслано"));
    }

    @PostMapping("/auth/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(userService.authenticate(request));
    }

    @PostMapping("/auth/verify")
    public ResponseEntity<?> verifyCode(@RequestBody String code) {
        try {
            AuthenticationResponse response = userService.verify(code);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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