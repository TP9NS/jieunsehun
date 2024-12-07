package com.example.ourapp.entity;

import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.ourapp.DTO.UserDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Entity
@Table(name = "user")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true, length = 30, nullable = false)
    private String username;

    @Column(name = "password", length = 30, nullable = false)
    private String password;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @Column(name = "email", length = 40)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "postcode", length = 200)
    private String postcode;
    
    @Column(name = "address", length = 200)
    private String address;
    
    @Column(name = "address_1", length = 200)
    private String address_1;
    
    @Column(name = "permission")
    private Integer permission;

    @Column(name="stopdate")
    private LocalDateTime date;
    
    public static User toUser(UserDTO UserDTO){
    	User user = new User();
    	user.setUsername(UserDTO.getUsername());
    	user.setEmail(UserDTO.getEmail());
    	user.setName(UserDTO.getName());
    	user.setPassword(UserDTO.getPassword());
    	user.setBirthdate(UserDTO.getBirthdate());
    	user.setPostcode(UserDTO.getPostcode());
    	user.setAddress(UserDTO.getAddress());
    	user.setAddress_1(UserDTO.getAddress_1());
    	user.setPhone(UserDTO.getPhone());
    	user.setPermission(UserDTO.getPermission());
        return user;
    }
}