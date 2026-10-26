package com.resilia.lre.service;

import com.resilia.lre.dto.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface LreService {

    // Server Configurations
    List<LreServerConfigDTO> getAllServerConfigs();
    List<LreServerConfigDTO> getActiveServerConfigs();
    Optional<LreServerConfigDTO> getServerConfigById(Long id);
    LreServerConfigDTO saveServerConfig(LreServerConfigDTO dto);
    void deleteServerConfig(Long id);
    boolean testConnection(Long serverConfigId);

    // LRE Metadata Exploration (JSON based)
    List<LreDomainDTO> getDomains(Long serverConfigId);
    List<LreProjectDTO> getProjects(Long serverConfigId, String domainName);
    List<LreTestDTO> getTests(Long serverConfigId, String domainName, String projectName);

    // Saved Run Configurations / Profiles
    List<LreSavedRunConfigDTO> getAllSavedProfiles();
    List<LreSavedRunConfigDTO> getSavedProfilesByServer(Long serverConfigId);
    Optional<LreSavedRunConfigDTO> getSavedProfileById(Long id);
    LreSavedRunConfigDTO saveProfile(LreSavedRunConfigDTO dto);
    void deleteProfile(Long id);

    // Execution & Async Runs
    CompletableFuture<LreExecutionHistoryDTO> executeTestAsync(
            Long serverConfigId,
            String domainName,
            String projectName,
            LreRunRequestDTO request,
            String testName,
            Long savedConfigId
    );

    CompletableFuture<LreExecutionHistoryDTO> executeSavedProfileAsync(Long savedProfileId);

    // Execution History & Status
    List<LreExecutionHistoryDTO> getRecentExecutions();
    Optional<LreExecutionHistoryDTO> getExecutionById(Long id);
    LreExecutionHistoryDTO refreshExecutionStatus(Long executionHistoryId);
}