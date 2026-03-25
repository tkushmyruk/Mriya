package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.Profile;
import org.example.service.MediaStorageService;
import org.example.service.ProfileService;
import org.springframework.web.bind.annotation.*;
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
