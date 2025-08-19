package com.cashcoach.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String type;

    private String icon;

    // when the category is fetched, profile will be null. Only gets profile, if u call getProfile. On eager, fetched category comes with profile
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="profile_id", nullable = false)
    private Profile profile;

}
