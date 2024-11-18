package com.example.ourapp.ImageTest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository  
public interface ImageRepository extends JpaRepository<Image, String> {  
}