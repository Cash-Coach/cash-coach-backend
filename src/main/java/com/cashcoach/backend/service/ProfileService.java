package com.cashcoach.backend.service;

import com.cashcoach.backend.dto.AuthDTO;
import com.cashcoach.backend.dto.ProfileDTO;
import com.cashcoach.backend.entity.Profile;
import com.cashcoach.backend.repository.ProfileRepository;
import com.cashcoach.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDTO addProfile(ProfileDTO profileDTO) {
        Profile userProfile = toEntity(profileDTO);
        userProfile.setActivationToken(UUID.randomUUID().toString());
        userProfile = profileRepository.save(userProfile);
        profileDTO = toDTO(userProfile);

        // send activation email
        String userEmailAddress = userProfile.getEmail();
        String activationLink = activationURL + "/api/activate?token=" + userProfile.getActivationToken();
        String subject = "Let's activate your Cash Coach account!";
        String body = "Hey " + userProfile.getFullName() + ",\n\nClick on the link below to activate your account:\n\n" + activationLink;
        emailService.sendEmail(userEmailAddress, subject, body);

        return profileDTO;
    }

    public Profile toEntity(ProfileDTO profileDTO) {

        return Profile.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profilePicUrl(profileDTO.getProfilePicUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();

    }

    public ProfileDTO toDTO(Profile profile) {
        // never return back the password
        return ProfileDTO.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .profilePicUrl(profile.getProfilePicUrl())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    public boolean isAccActive(String email) {
        return profileRepository.findByEmail(email)
                .map(Profile::getIsActive)
                .orElse(false);
    }

    public Profile getCurrentProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with the email: " + email));
    }

    public ProfileDTO getPublicProfile(String email) {
        Profile currentUser = null;
        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with the email: " + email));
        }

        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profilePicUrl(currentUser.getProfilePicUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            // Generate JWT Token
            User userDetails = new User(authDTO.getEmail(), "", new ArrayList<>());
            String token = jwtUtil.generateToken(userDetails);
            System.out.println(token);
            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDTO.getEmail())
            );
        } catch (Exception e){
            throw new RuntimeException("Invalid email or password");
        }
    }
}
