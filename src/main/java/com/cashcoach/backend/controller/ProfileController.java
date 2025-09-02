package com.cashcoach.backend.controller;

import com.cashcoach.backend.dto.AuthDTO;
import com.cashcoach.backend.dto.ProfileDTO;
import com.cashcoach.backend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/add")
    public ResponseEntity<ProfileDTO> addProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO addedProfile = profileService.addProfile(profileDTO);
        ResponseEntity<ProfileDTO> response = new ResponseEntity<ProfileDTO>(addedProfile, HttpStatus.CREATED);

        return response;
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
         boolean isActivated = profileService.activateProfile(token);
         if (isActivated) {
            String successMessage = "Profile activated successfully!";
            return ResponseEntity.ok(successMessage);
         } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or used already");
         }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
        try {
            if (!profileService.isAccActive(authDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "Account is not active. Please activate your account first."
                ));
            }
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage()
            ));

        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getPublicProfile() {
        ProfileDTO profileDTO = profileService.getPublicProfile(null);
        return ResponseEntity.ok(profileDTO);
    }

    @PutMapping("/profile-pic/{profileId}")
    public ResponseEntity<ProfileDTO> updateProfile(@PathVariable Long profileId, @RequestBody ProfileDTO profileDTO) {
        ProfileDTO updatedProfile = profileService.updateProfile(profileId, profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

}
