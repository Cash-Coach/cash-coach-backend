package com.cashcoach.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthDTO {

    private String email;
    private String password;
    private String token;



}
