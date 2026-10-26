package com.resilia.lre.repository;

import com.resilia.lre.entity.LreSavedRunConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LreSavedRunConfigRepository extends JpaRepository<LreSavedRunConfigEntity, Long> {
    List<LreSavedRunConfigEntity> findByServerConfigId(Long serverConfigId);
    List<LreSavedRunConfigEntity> findByDomainNameAndProjectName(String domainName, String projectName);
}