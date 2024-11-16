package com.example.ourapp.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.ourapp.DTO.CategoryDTO;
import com.example.ourapp.entity.Category;

@Service
public class CategoryService {

    @Autowired
    private PostRepository postRepository;

    public List<CategoryDTO> getAllCategoryHierarchy() {
        List<Category> categories = postRepository.findAllCategoriesWithHierarchy(); // 계층형 데이터 조회
        Map<Long, CategoryDTO> categoryMap = new HashMap<>();

        // 모든 카테고리를 DTO로 변환
        categories.forEach(category -> {
            CategoryDTO dto = new CategoryDTO(category);
            categoryMap.put(dto.getId(), dto);
        });

        // 계층 구조 생성
        List<CategoryDTO> rootCategories = new ArrayList<>();
        categoryMap.values().forEach(dto -> {
            if (dto.getParentId() == null) {
                rootCategories.add(dto); // 상위 카테고리
            } else {
                CategoryDTO parent = categoryMap.get(dto.getParentId());
                if (parent != null) {
                    parent.addSubCategory(dto); // 하위 카테고리를 상위 카테고리에 추가
                }
            }
        });

        return rootCategories;
    }
}
