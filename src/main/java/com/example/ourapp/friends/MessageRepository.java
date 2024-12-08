package com.example.ourapp.friends;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ourapp.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	 List<Message> findByReceiverId(String receiverId);
	 Long countByReceiverIdAndStatus(String receiverId, Message.MessageStatus status);
}
