package com.resilia.repository;

import com.resilia.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
    List<TestEntity> findByDomainAndProject(String domain, String project);
    Optional<TestEntity> findByIdAndDomainAndProject(Long id, String domain, String project);
}
