package com.cashcoach.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;

    private Long profileId;

    private String name;

    private String icon;

    private String type;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
