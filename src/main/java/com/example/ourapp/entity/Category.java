package com.example.ourapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 카테고리 이름

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory; // 상위 카테고리

    // Getter 및 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getParentCategory() { return parentCategory; }
    public void setParentCategory(Category parentCategory) { this.parentCategory = parentCategory; }
}
