package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.Profile;
import org.example.repository.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public Profile getProfileByUserId(Integer userId) {
        return profileRepository.findById(userId).get();
    }

    public void saveProfile(Profile profile) {
        profileRepository.save(profile);
    }

}
