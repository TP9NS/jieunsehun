package com.example.ourapp.map;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ourapp.DTO.VoteResultDTO;
import com.example.ourapp.entity.Vote;

@Service
public class VoteService {

    private final VoteRepository voteRepository;

    @Autowired
    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public void saveVote(Long userId, String locationName) {
        Vote vote = new Vote(userId, locationName, LocalDateTime.now());
        voteRepository.save(vote);
    }

    public List<Vote> getVotesByUserId(Long userId) {
        return voteRepository.findByUserId(userId);
    }
    public List<VoteResultDTO> calculateVoteResults() {
        // 장소별 투표 수 계산
        List<Object[]> rawResults = voteRepository.findVoteCountsByLocation();

        // 투표 결과 DTO로 변환
        List<VoteResultDTO> results = rawResults.stream()
                .map(result -> {
                    String locationName = (String) result[0];
                    int voteCount = ((Number) result[1]).intValue();
                    String imageName = locationName.replace(" ", "-") + ".jpg"; // 이미지 이름 생성
                    return new VoteResultDTO(locationName, voteCount, imageName);
                })
                .sorted(Comparator.comparingInt(VoteResultDTO::getVoteCount).reversed()) // 투표 수로 내림차순 정렬
                .collect(Collectors.toList());

        // 공동 순위 계산
        int rank = 0;
        int previousVoteCount = -1;

        for (int i = 0; i < results.size(); i++) {
            VoteResultDTO currentResult = results.get(i);
            if (currentResult.getVoteCount() != previousVoteCount) {
                rank = i + 1; // 이전 투표 수와 다르면 새로운 순위 할당
                previousVoteCount = currentResult.getVoteCount();
            }
            currentResult.setRank(rank); // 공동 순위 할당
        }

        return results.stream()
                .filter(result -> result.getRank() <= 3) // 상위 3등까지만 필터링
                .collect(Collectors.toList());
    }
}
