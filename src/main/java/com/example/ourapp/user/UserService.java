package com.example.ourapp.user;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ourapp.DTO.UserDTO;



import com.example.ourapp.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service //스프링이 관리해주는 객체 == 스프링 빈
@RequiredArgsConstructor //controller와 같이. final 멤버변수 생성자 만드는 역할 
@Transactional
public class UserService {

    private final UserRepository userRepository; // 먼저 jpa, mysql dependency 추가

    public void save(UserDTO UserDTO) {
        // repsitory의 save 메서드 호출
    	User userEntity= new User();
    	UserDTO.setPermission(1);// 일반 회원 permission = 1 , 관리자 = 2
        userEntity = userEntity.toUser(UserDTO);
        userRepository.save(userEntity);
        //Repository의 save메서드 호출 (조건. entity객체를 넘겨줘야 함)
    }
    
    // 로그인 검증 로직
    public UserDTO login(String username, String password) {
        // username으로 사용자 검색
        Optional<User> optionalUser = userRepository.findByUsername(username);
        
        // 사용자가 존재하지 않을 경우
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = optionalUser.get(); // 존재하는 사용자 객체 가져오기
        
        // 비밀번호 확인
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password");
        }
        
        // User 엔티티를 UserDTO로 변환하여 반환
        return UserDTO.toUserDTO(user);
    }

    public UserDTO findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return UserDTO.toUserDTO(user.get());
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
    public void updateUser(UserDTO userDTO) {
        if (userDTO.getUser_id() == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        Optional<User> userOptional = userRepository.findById(userDTO.getUser_id());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Log values before updating
            System.out.println("Updating user: " + userDTO);
            userDTO.updateUserEntity(user);
            userRepository.save(user);
        } else {
            throw new EntityNotFoundException("User not found with ID: " + userDTO.getUser_id());
        }
    }
    
    public boolean checkUsernameDuplicate(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent();
    }
    
    public Optional<User> findByNameAndEmail(String name, String email) {
        return userRepository.findByNameAndEmail(name, email);
    }

    public Optional<User> findByUsernameAndNameAndBirthdateAndEmail(String username, String name, LocalDate birthdate, String email) {
        return Optional.ofNullable(userRepository.findByUsernameAndNameAndBirthdateAndEmail(username, name, birthdate, email));
    }
}


