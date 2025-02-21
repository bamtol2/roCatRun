package com.ssafy.roCatRun.domain.boss.service;

import com.ssafy.roCatRun.domain.boss.dto.response.BossResponse;
import com.ssafy.roCatRun.domain.boss.entity.Boss;
import com.ssafy.roCatRun.domain.boss.repository.BossRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BossService {
    private final BossRepository bossRepository;

    @Transactional(readOnly = true)
    public BossResponse getBossInfo(){
        List<Boss> bossList = bossRepository.findAll();
        if(bossList.isEmpty()){
            return new BossResponse(null);
        }

        List<BossResponse.Boss> bossResponses = new ArrayList<>();
        for (Boss boss : bossList) {
            BossResponse.Boss bossDto = BossResponse.Boss.builder()
                    .difficulty(boss.getDifficulty())
                    .timeLimit(boss.getTimeLimit())
                    .hpPerKm(boss.getHpPerKm())
                    .distance(boss.getDistance())
                    .bossImage(boss.getBossImage())
                    .bossName(boss.getBossName())
                    .expRewardMin(boss.getExpRewardMin())
                    .expRewardMax(boss.getExpRewardMax())
                    .feverCondition(boss.getFeverCondition())
                    .coinRewardMin(boss.getCoinRewardMin())
                    .coinRewardMax(boss.getCoinRewardMax())
                    .build();
            bossResponses.add(bossDto);
        }

        return new BossResponse(bossResponses);
    }
}
