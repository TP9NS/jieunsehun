package com.example.ourapp.DTO;

public class ChatMessageDTO {
    private String sender; // 메시지를 보낸 사람
    private String content; // 메시지 내용
    private String groupId; // 그룹 ID

    // Getters and Setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
