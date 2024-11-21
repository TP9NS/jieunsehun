package com.example.ourapp.group;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ourapp.entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
}
