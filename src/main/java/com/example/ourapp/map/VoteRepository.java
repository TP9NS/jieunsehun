package com.example.ourapp.map;

import com.example.ourapp.entity.Vote;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    // 사용자 ID로 투표 기록 조회
    List<Vote> findByUserId(Long userId);
    @Query("SELECT v.locationName, COUNT(v) FROM Vote v GROUP BY v.locationName ORDER BY COUNT(v) DESC")
    List<Object[]> findVoteCountsByLocation();
}
