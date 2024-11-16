package com.example.ourapp.user;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ourapp.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByEmail(String Email);
	Optional<User> findByUsername(String username);
	User findByUserId(Long UserId);
	User findByNameAndBirthdateAndEmail(String name, LocalDate birthdate, String email);
	User findByUsernameAndNameAndBirthdateAndEmail(String Username,String name, LocalDate birthdate, String email);
	Optional<User> findByNameAndEmail(String name, String email);
	 List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}