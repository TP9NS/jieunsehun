package com.example.ourapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDetailDTO {
    private Long requestId; // FriendRequest ID
    private Long senderId;  // 요청 보낸 사용자 ID
    private String senderName; // 요청 보낸 사용자 이름
    private String senderEmail; // 요청 보낸 사용자 이메일
}
