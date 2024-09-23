package com.example.ourapp.user;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ourapp.DTO.UserDTO;



import com.example.ourapp.entity.*;
import jakarta.persistence.EntityManager;
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
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 입력된 비밀번호와 DB의 비밀번호가 일치하는지 확인
            if (user.getPassword().equals(password)) {
                // User 엔티티를 UserDTO로 변환하여 반환
                return UserDTO.toUserDTO(user);
            } else {
                throw new IllegalArgumentException("Invalid password");
            }
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }


}