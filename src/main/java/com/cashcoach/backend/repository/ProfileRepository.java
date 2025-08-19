package com.cashcoach.backend.repository;

import com.cashcoach.backend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    // select * from profile where email = ?
    Optional<Profile> findByEmail(String email); // argument will replace the question mark

    // select * from profile where activation_token = ?
    Optional<Profile> findByActivationToken(String activationToken);

}
