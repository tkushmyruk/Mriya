package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.domain.nosql.Post;
import org.example.domain.sql.Profile;
import org.example.domain.sql.Role;
import org.example.domain.sql.User;
import org.example.domain.sql.VerificationCode;
import org.example.dto.AuthenticationRequest;
import org.example.dto.AuthenticationResponse;
import org.example.dto.RegisterRequest;
import org.example.dto.UpdateProfileRequest;
import org.example.repository.sql.UserRepository;
import org.example.repository.sql.VerificationCodeRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ProfileService profileService;
    private final JavaMailSender mailSender;
    private final VerificationCodeRepository codeRepository;

    private final MongoTemplate mongoTemplate;

    @Transactional
    public User updateProfile(String email, UpdateProfileRequest request) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.getProfile().setFirstName(request.getFirstName());
        user.getProfile().setLastName(request.getLastName());

        repository.save(user);

        Query query = new Query(Criteria.where("authorId").is(user.getId()));
        Update update = new Update()
                .set("authorFirstName", request.getFirstName())
                .set("authorLastName", request.getLastName());

        mongoTemplate.updateMulti(query, update, Post.class);

        return user;
    }

    @Transactional
    public void register(RegisterRequest request) {
        Profile profile = Profile.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        profileService.saveProfile(profile);

        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .profile(profile)
                .role(Role.USER)
                .isEnabled(false)
                .build();

        repository.save(user);

        sendVerificationCode(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        if (!user.isEnabled()) {
            throw new RuntimeException("USER_DISABLED");
        }
        return new AuthenticationResponse(jwtToken, user.getId());
    }

    @Transactional
    public AuthenticationResponse verify(String code) {
        VerificationCode vc = codeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Невірний код"));

        if (vc.isExpired()) {
            codeRepository.delete(vc);
            throw new RuntimeException("Термін дії коду вичерпано");
        }

        User user = vc.getUser();
        user.setEnabled(true);
        repository.save(user);
        codeRepository.delete(vc);

        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken, user.getId());
    }

    @Async
    @Transactional
    public void sendVerificationCode(User user) {
        String code = String.format("%06d", new Random().nextInt(1000000));

        VerificationCode verificationCode = codeRepository.findByUserId(user.getId()).orElse(new VerificationCode());

        verificationCode.setUser(user);
        verificationCode.setCode(code);
        verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        codeRepository.save(verificationCode);

        sendEmail(user.getEmail(), code);
    }

    private void sendEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Mriya: Ваш код підтвердження");
        message.setText("Ваш код для реєстрації: " + code + ". Він дійсний 15 хвилин.");
        mailSender.send(message);
    }
}
