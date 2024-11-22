package com.example.ourapp.group;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ourapp.entity.Group;
import com.example.ourapp.entity.GroupMember;
import com.example.ourapp.entity.User;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
	List<GroupMember> findByGroup_GroupId(Long groupId);
	Optional<GroupMember> findByGroup_groupIdAndUser_userId(Long groupId, Long userId);
	List<GroupMember> findByUser(User user);
	 List<GroupMember> findByGroup(Group group);
}
