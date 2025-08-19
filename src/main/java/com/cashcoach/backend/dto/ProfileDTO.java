package com.cashcoach.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {

    private Long id;

    private String fullName;

    private String email;

    private String password;

    private String profilePicUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
