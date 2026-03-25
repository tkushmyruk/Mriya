package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.sql.Profile;
import org.example.service.MediaStorageService;
import org.example.service.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/profile/{userId}")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final MediaStorageService mediaStorageService;

    @GetMapping
    public Profile getProfile(@PathVariable int userId) {
     return profileService.getProfileByUserId(userId);
    }

    @PostMapping("/upload-avatar")
    public Profile uploadAvatar(
            @PathVariable int userId,
            @RequestParam("file") MultipartFile file
    ) {
        mediaStorageService.uploadMedia(file, userId);
        return profileService.getProfileByUserId(userId);

    }
}
