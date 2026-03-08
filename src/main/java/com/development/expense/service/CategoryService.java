package com.development.expense.service;

import com.development.expense.constant.CodeConstant;
import com.development.expense.constant.MessageConstant;
import com.development.expense.dto.ApiResponse;
import com.development.expense.dto.CategoryDto;
import com.development.expense.entity.CategoryEntity;
import com.development.expense.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${cached.category.key}")
    String cachedCategoryKey;
    @Value("${cached.category.ttl}")
    Integer cachedCategoryTtl;

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ApiResponse getAllCategories(int page, int size) {
        ApiResponse response = new ApiResponse();
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CategoryEntity> data = categoryRepository.findAll(pageable);

            for (CategoryEntity category : data) {
                categoryDtoList.add(new CategoryDto(category.getId(), category.getName()));
            }
        } catch (Exception e) {
            response.setCode(CodeConstant.INTERNAL_SERVER_ERROR);
            response.setMessage(MessageConstant.INTERNAL_SERVER_ERROR);
            return response;
        }
        response.setCode(CodeConstant.SUCCESS);
        response.setMessage("Get All Categories");
        response.setData(categoryDtoList);
        return response;
    }

    public ApiResponse getCategoryById(Long id) {
        ApiResponse response = new ApiResponse();
        var cached = redisTemplate.opsForValue().get(cachedCategoryKey + id);
        ObjectMapper mapper = new ObjectMapper();
        CategoryEntity data = mapper.convertValue(cached, CategoryEntity.class);
        if (data == null) {
            data = categoryRepository.findById(id).orElse(null);
            if (data != null) {
                redisTemplate.opsForValue().set(cachedCategoryKey + id, data, Duration.ofMinutes(cachedCategoryTtl));
                System.out.println("SAVE INTO CACHED");
            }
            System.out.println("FROM DATABASE");
        } else {
            System.out.println("FROM CACHED");
        }
        if (data == null) {
            response.setCode(CodeConstant.NOT_FOUND);
            response.setMessage(MessageConstant.NOT_FOUND);
            return response;
        }
        response.setCode(CodeConstant.SUCCESS);
        response.setMessage(MessageConstant.SUCCESS);
        CategoryDto categoryDto = new CategoryDto(data.getId(), data.getName());
        response.setData(categoryDto);
        return response;
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
        redisTemplate.opsForValue().set(cachedCategoryKey + category.getId(), category, Duration.ofMinutes(cachedCategoryTtl));
        System.out.println("SAVE INTO CACHED");
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
        redisTemplate.opsForValue().getAndDelete(cachedCategoryKey + find.get().getId());
        return "updated";
    }

    public String delete(Long id) {
        var find = categoryRepository.findById(id);
        if (find.isEmpty()) {
            return "Id not found";
        }
        categoryRepository.delete(find.get());
        redisTemplate.opsForValue().getAndDelete(cachedCategoryKey + id);
        return "deleted";
    }
}