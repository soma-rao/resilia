package com.resilia.repository;

import com.resilia.entity.RunResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RunResultRepository extends JpaRepository<RunResultEntity, Long> {
    List<RunResultEntity> findByRunId(Long runId);
}
