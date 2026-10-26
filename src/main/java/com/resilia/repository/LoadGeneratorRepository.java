package com.resilia.repository;

import com.resilia.entity.LoadGeneratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoadGeneratorRepository extends JpaRepository<LoadGeneratorEntity, Long> {
    List<LoadGeneratorEntity> findByDomainAndProject(String domain, String project);
}
