package com.ssafy.roCatRun.domain.gameCharacter.dto.response;

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
}