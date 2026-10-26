package com.resilia.repository;

import com.resilia.entity.TestSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSetRepository extends JpaRepository<TestSetEntity, Long> {
    List<TestSetEntity> findByDomainAndProject(String domain, String project);
}
