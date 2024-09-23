package com.example.ourapp.DTO;

import java.time.LocalDate;

import com.example.ourapp.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//lombok dependency추가
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDTO { //회원 정보를 필드로 정의
    private Long User_id;
	private String Username;
    private String Email;
    private String Password;
    private String Name;
    private LocalDate birthdate;
    private String phone;
    private String address;
    private String postcode;
    private String address_1;
    private Integer permission;
    //lombok 어노테이션으로 getter,setter,생성자,toString 메서드 생략 가능
    
    public static UserDTO toUserDTO(User UserEntitiy){
    	UserDTO UserDTO = new UserDTO();
    	UserDTO.setUser_id (UserEntitiy.getUserId());
    	UserDTO.setUsername(UserEntitiy.getUsername());
    	UserDTO.setEmail(UserEntitiy.getEmail());
    	UserDTO.setName(UserEntitiy.getName());
    	UserDTO.setPassword(UserEntitiy.getPassword());
    	UserDTO.setBirthdate(UserEntitiy.getBirthdate());
    	UserDTO.setAddress(UserEntitiy.getAddress());
    	UserDTO.setPostcode(UserEntitiy.getPostcode());
    	UserDTO.setAddress_1(UserEntitiy.getAddress_1());
    	UserDTO.setPhone(UserEntitiy.getPhone());
    	UserDTO.setPermission(UserEntitiy.getPermission());
        return UserDTO;
    }
}