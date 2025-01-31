package com.ssafy.raidtest.raid.repository;

import com.ssafy.raidtest.raid.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

}
