package com.cashcoach.backend.service;

import com.cashcoach.backend.dto.CategoryDTO;
import com.cashcoach.backend.entity.Category;
import com.cashcoach.backend.entity.Profile;
import com.cashcoach.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    // save category
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        Profile userProfile = profileService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), userProfile.getId())) {
            throw new RuntimeException("Category with this name already exists");
        }

        Category uploadCategory = toEntity(categoryDTO, userProfile);
        uploadCategory = categoryRepository.save(uploadCategory);
        return toDTO(uploadCategory);
    }

    // get categories for current user
    public List<CategoryDTO> getUserCategories() {
        Profile userProfile = profileService.getCurrentProfile();
        List<CategoryDTO> categoryCollection = new ArrayList<>();
        for (Category category : categoryRepository.findByProfileId(userProfile.getId())) {
            CategoryDTO categoryDTO = toDTO(category);
            categoryCollection.add(categoryDTO);
        }
        return categoryCollection;
    }

    // get categories by type for current user
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        Profile userProfile = profileService.getCurrentProfile();
        List<Category> categoryCollection = categoryRepository.findByTypeAndProfileId(type, userProfile.getId());
        return categoryCollection.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Profile userProfile = profileService.getCurrentProfile();
        Category userCategory = categoryRepository.findByIdAndProfileId(categoryId, userProfile.getId())
                .orElseThrow(() -> new RuntimeException("The category either cannot be found or cannot be accessed"));
        userCategory.setName(categoryDTO.getName());
        userCategory.setIcon(categoryDTO.getIcon());
        userCategory.setUpdatedAt(LocalDateTime.now());
        userCategory = categoryRepository.save(userCategory);
        return toDTO(userCategory);
    }

    public void deleteCategory(Long categoryId) {
        Profile userProfile = profileService.getCurrentProfile();
        Category deletedCategory = categoryRepository.findByIdAndProfileId(categoryId, userProfile.getId())
                .orElseThrow(() -> new RuntimeException("The category either cannot be found or cannot be accessed"));
        if (!deletedCategory.getProfile().getId().equals(userProfile.getId())) {
            throw new RuntimeException("Not authorized to delete this income");
        }

        try {
            categoryRepository.delete(deletedCategory);
        } catch (DataIntegrityViolationException e) {
            // Check if it's a foreign key constraint violation
            if (e.getCause() != null) {
                throw new RuntimeException("You must delete all transactions relating to this category before you delete it");
            }
            // Re-throw if it's a different type of data integrity issue
            throw e;
        }
    }

    // helper methods
    private Category toEntity(CategoryDTO categoryDTO, Profile profile) {
        return Category.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .profileId(category.getProfile() != null ? category.getProfile().getId() : null)
                .name(category.getName())
                .icon(category.getIcon())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .type(category.getType())
                .build();
    }


}
