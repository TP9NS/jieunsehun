package com.example.ourapp.friends;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ourapp.entity.Friends;
import com.example.ourapp.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {
    List<Friends> findByUser(User user);

    Optional<Friends> findByUserAndFriend(User user, User friend);
}
