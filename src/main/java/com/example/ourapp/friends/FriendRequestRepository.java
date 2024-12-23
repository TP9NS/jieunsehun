package com.example.ourapp.friends;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ourapp.entity.FriendRequest;
import com.example.ourapp.entity.User;
import com.example.ourapp.friends.FriendRequestStatus;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequestStatus status);
    boolean existsBySenderAndReceiver(User sender, User receiver);
    long countByReceiverAndStatus(User user, FriendRequestStatus status);
}
