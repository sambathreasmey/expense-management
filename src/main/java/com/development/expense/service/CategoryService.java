package com.development.expense.service;

import com.development.expense.dto.CategoryDto;
import com.development.expense.entity.CategoryEntity;
import com.development.expense.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getAllCategories() {
        List<CategoryEntity> data = categoryRepository.findAll();
        List<CategoryDto> response = new ArrayList<>();
        for (CategoryEntity category : data) {;
            response.add(new CategoryDto(category.getId(), category.getName()));
        }
        return response;
    }

    public CategoryEntity getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public String add(CategoryEntity category) {
        if (category.getName() == null || category.getName().isEmpty()) {
            return "name is empty";
        }
        var find = categoryRepository.findByName(category.getName());
        if (find != null) {
            return "name already exists";
        }
        category.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        categoryRepository.save(category);
        return "inserted";
    }

    public String update(CategoryEntity category) {
        if (category.getName() == null || category.getName().isEmpty()) {
            return "name is not null or empty";
        }
        Optional<CategoryEntity> find = categoryRepository.findById(category.getId());
        if (find.isEmpty()) {
            return "Id not found";
        }
        category.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        category.setCreatedAt(find.get().getCreatedAt());
        categoryRepository.save(category);
        return "updated";
    }

    public String delete(Long id) {
        var find = categoryRepository.findById(id);
        if (find.isEmpty()) {
            return "Id not found";
        }
        categoryRepository.delete(find.get());
        return "deleted";
    }
}