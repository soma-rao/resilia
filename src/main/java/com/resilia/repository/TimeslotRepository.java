package com.resilia.repository;

import com.resilia.entity.TimeslotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeslotRepository extends JpaRepository<TimeslotEntity, Long> {
    List<TimeslotEntity> findByDomainAndProject(String domain, String project);
}
