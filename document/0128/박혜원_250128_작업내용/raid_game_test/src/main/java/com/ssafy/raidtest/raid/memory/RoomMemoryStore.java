package com.ssafy.raidtest.raid.memory;

import com.ssafy.raidtest.raid.domain.boss.Boss;
import com.ssafy.raidtest.raid.domain.room.RaidRoom;
import com.ssafy.raidtest.raid.domain.room.RoomStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RoomMemoryStore {
    private final Map<String, RaidRoom> roomMap = new ConcurrentHashMap<>();
    private final Map<String, String> inviteCodeMap = new ConcurrentHashMap<>(); // 초대코드를 통한 roomId 찾기

    // 방 아이디로 방 조회
    public Optional<RaidRoom> getRoom(String roomId){
        return Optional.ofNullable(roomMap.get(roomId));
    }

    // 방 생성(방 정보 저장)
    public void saveRoom(RaidRoom room){
        roomMap.put(room.getRoomId(), room); // 룸 생성 및 룸 정보 저장
        if(room.getInviteCode() != null){ // 룸의 초대 코드가 null이 아니면 (초대코드, 방아이디) 저장
            inviteCodeMap.put(room.getInviteCode(), room.getRoomId());
        }
    }

    // 초대 코드로 방 정보 조회
    public Optional<RaidRoom> findByInviteCode(String inviteCode){
        String roomId = inviteCodeMap.get(inviteCode);
        return Optional.ofNullable(roomMap.get(roomId));
    }

    // 랜덤매칭
    public Optional<RaidRoom> findAvailableRoom(Boss boss, int maxPlayers){
        return roomMap.values().stream()
                .filter(room->room.getStatus()== RoomStatus.WAITING)
                .filter(room -> room.getBoss().getId().equals(boss.getId()))
                .filter(room->room.getMaxPlayers()==maxPlayers)
                .filter(room->room.getPlayerIds().size()<maxPlayers)
                .findFirst();
    }

    public void removeRoom(String roomId){
        RaidRoom room = roomMap.remove(roomId);
        if(room!=null&&room.getInviteCode()!=null){
            inviteCodeMap.remove(room.getInviteCode());
        }
    }
}
