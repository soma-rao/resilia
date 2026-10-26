package com.resilia.lre.repository;

import com.resilia.lre.entity.LreServerConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LreServerConfigRepository extends JpaRepository<LreServerConfigEntity, Long> {
    Optional<LreServerConfigEntity> findByName(String name);
    List<LreServerConfigEntity> findByActiveTrue();
}