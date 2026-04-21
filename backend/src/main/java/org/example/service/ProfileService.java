package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.sql.Profile;
import org.example.repository.sql.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findById(userId).get();
    }

    public Profile getProfileById(Long id) {
        return profileRepository.findById(id).orElse(null);
    }

    public Profile getProfileNameByUserId(Long userId) {
        return profileRepository.findById(userId).get();
    }

    public List<Profile> search(String query) {
        return profileRepository.searchProfiles(query);
    }

    public void saveProfile(Profile profile) {
        profileRepository.save(profile);
    }

}
