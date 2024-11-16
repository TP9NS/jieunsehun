package com.example.ourapp.DTO;

import java.util.ArrayList;
import java.util.List;

import com.example.ourapp.entity.Category;

public class CategoryDTO {

    private Long id;
    private String name;
    private Long parentId;
    private List<CategoryDTO> subCategories = new ArrayList<>();

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.parentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;
    }

    public void addSubCategory(CategoryDTO subCategory) {
        this.subCategories.add(subCategory);
    }

    // Getter 및 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public List<CategoryDTO> getSubCategories() { return subCategories; }
    public void setSubCategories(List<CategoryDTO> subCategories) { this.subCategories = subCategories; }
}
