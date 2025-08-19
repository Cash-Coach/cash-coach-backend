package com.cashcoach.backend.repository;

import com.cashcoach.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // select * from categories where profile_id = ?
    List<Category> findByProfileId(Long profileId);

    // select * from categories where id = ? and profile_id = ?
    Optional<Category> findByIdAndProfileId(Long id, Long profileId);

    // select * from categories where type = ? and profile_id = ?
    List<Category> findByTypeAndProfileId(String type, Long profileId);

    Boolean existsByNameAndProfileId(String name, Long ProfileId);

}
