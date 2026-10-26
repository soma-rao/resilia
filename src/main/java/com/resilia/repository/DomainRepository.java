package com.resilia.repository;

import com.resilia.entity.DomainEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DomainRepository extends JpaRepository<DomainEntity, Long> {
    Optional<DomainEntity> findByName(String name);
}
