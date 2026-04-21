package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.sql.Profile;
import org.example.domain.sql.User;
import org.example.dto.UpdateProfileRequest;
import org.example.repository.sql.UserRepository;
import org.example.service.MediaStorageService;
import org.example.service.ProfileService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final MediaStorageService mediaStorageService;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/me")
    public Profile getMyProfile(Principal principal) {
        User user = getUserByPrincipal(principal);
        return profileService.getProfileByUserId(user.getId());
    }

    @GetMapping("/public/{profileId}")
    public Profile getPublicProfile(@PathVariable long profileId) {
        return profileService.getProfileById(profileId);
    }

    @GetMapping("/public/user/{userId}")
    public Profile getPublicProfileByUserId(@PathVariable long userId) {
        return profileService.getProfileNameByUserId(userId);
    }

    @PostMapping("/me/upload-avatar")
    public Profile uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        User user = getUserByPrincipal(principal);
        long userId = user.getId();

        mediaStorageService.uploadMedia(file, userId);
        return profileService.getProfileByUserId(userId);
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateProfile(@RequestBody UpdateProfileRequest request, Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(userService.updateProfile(email, request));
    }

    @GetMapping("/search")
    public List<Profile> search(@RequestParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return profileService.search(query);
    }

    private User getUserByPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}