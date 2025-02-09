package com.ssafy.roCatRun.domain.character.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankingListResponse {
    private RankingResponse myRanking;           // 내 랭킹 정보
    private List<RankingResponse> rankings;      // 전체 랭킹 리스트
    private boolean hasNext;                     // 다음 페이지 존재 여부
}