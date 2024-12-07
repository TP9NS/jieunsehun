package com.example.ourapp.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime stopDate;
    //lombok 어노테이션으로 getter,setter,생성자,toString 메서드 생략 가능
    public UserDTO(Long id, String name, String email) {
        this.User_id = id;
        this.Name = name;
        this.Email = email;
    }
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
    	UserDTO.setStopDate(UserEntitiy.getDate());
        return UserDTO;
    }
    public void updateUserEntity(User userEntity) {
        if (userEntity != null) {
            if (this.Username != null) {
                userEntity.setUsername(this.Username);
            }
            if (this.Email != null) {
                userEntity.setEmail(this.Email);
            }
            if (this.Password != null) {
                userEntity.setPassword(this.Password);
            }
            userEntity.setName(this.Name);
            userEntity.setBirthdate(this.birthdate);
            userEntity.setPhone(this.phone);
            userEntity.setAddress(this.address);
            userEntity.setPostcode(this.postcode);
            userEntity.setAddress_1(this.address_1);
            if (this.permission != null) {
                userEntity.setPermission(this.permission);
            }
        }
    }
    
    public Long getUserId() {
        return User_id;
    }

    // 다른 getter, setter 메서드들...
    public void setUserId(Long userId) {
        this.User_id = userId;
    }
}