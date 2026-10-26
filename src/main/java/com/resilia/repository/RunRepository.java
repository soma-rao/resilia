package com.resilia.repository;

import com.resilia.entity.RunEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RunRepository extends JpaRepository<RunEntity, Long> {
    List<RunEntity> findByDomainAndProject(String domain, String project);
    List<RunEntity> findByDomainAndProjectAndStatus(String domain, String project, String status);
    Optional<RunEntity> findByIdAndDomainAndProject(Long id, String domain, String project);
}
