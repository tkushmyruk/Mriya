package org.example.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.example.repository.sql.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MediaStorageService {

    private final Cloudinary cloudinary;
    private final ProfileRepository profileRepository;

    @Transactional
    public String uploadMedia(MultipartFile file, long userId) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String mediaUrl = (String) uploadResult.get("secure_url");
            profileRepository.updateProfileUrlById(userId, mediaUrl);
            return mediaUrl;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload media", e);
        }
    }
}
