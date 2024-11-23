package com.example.ourapp.group;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ourapp.entity.Group;
import com.example.ourapp.entity.GroupRequest;
import com.example.ourapp.entity.User;

@Repository
public interface GroupRequestRepository extends JpaRepository<GroupRequest, Long> {
    List<GroupRequest> findByGroupAndStatus(Group group, GroupRequest.RequestStatus status);
    List<GroupRequest> findByUserAndStatus(User user, GroupRequest.RequestStatus status);
    // 여러 그룹에 대한 신청 목록을 반환
    List<GroupRequest> findByGroupIn(List<Group> groups);
    List<GroupRequest> findByGroupInAndStatus(List<Group> groups, GroupRequest.RequestStatus status);

}
