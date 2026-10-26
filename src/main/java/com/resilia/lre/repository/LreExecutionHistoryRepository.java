package com.resilia.lre.repository;

import com.resilia.lre.entity.LreExecutionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LreExecutionHistoryRepository extends JpaRepository<LreExecutionHistoryEntity, Long> {
    List<LreExecutionHistoryEntity> findTop50ByOrderByInitiatedAtDesc();
    List<LreExecutionHistoryEntity> findBySavedConfigIdOrderByInitiatedAtDesc(Long savedConfigId);
    List<LreExecutionHistoryEntity> findByServerConfigIdOrderByInitiatedAtDesc(Long serverConfigId);
}