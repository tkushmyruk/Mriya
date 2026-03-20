package org.example.controller;

import org.example.domain.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping
    public Profile getProfile() {
        System.out.println("ProfileController.getProfile called");
        return new Profile("Start");
    }
}
