package com.example.ourapp.chat;

import com.example.ourapp.entity.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
    List<GroupChat> findByGroupIdOrderByTimestampAsc(Long groupId);
}
