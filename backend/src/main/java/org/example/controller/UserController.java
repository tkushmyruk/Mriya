package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.dto.AuthenticationRequest;
import org.example.dto.AuthenticationResponse;
import org.example.dto.RegisterRequest;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/auth/register")
    public String register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/auth/authenticate")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) {
        return new AuthenticationResponse(userService.authenticate(request));
    }
}
