package com.example.ourapp.group;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ourapp.entity.Group;
import com.example.ourapp.entity.User;

public interface GroupRepository extends JpaRepository<Group, Long> {
	List<Group> findByGroupNameContainingIgnoreCase(String keyword);
	List<Group> findByCreatedBy(User createdBy);
	
}
