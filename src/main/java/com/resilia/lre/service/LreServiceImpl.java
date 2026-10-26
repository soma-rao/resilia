package com.resilia.lre.service;

import com.resilia.lre.client.LreApiClient;
import com.resilia.lre.dto.*;
import com.resilia.lre.entity.LreExecutionHistoryEntity;
import com.resilia.lre.entity.LreSavedRunConfigEntity;
import com.resilia.lre.entity.LreServerConfigEntity;
import com.resilia.lre.repository.LreExecutionHistoryRepository;
import com.resilia.lre.repository.LreSavedRunConfigRepository;
import com.resilia.lre.repository.LreServerConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class LreServiceImpl implements LreService {

    private static final Logger log = LoggerFactory.getLogger(LreServiceImpl.class);

    private final LreServerConfigRepository serverConfigRepository;
    private final LreSavedRunConfigRepository savedRunConfigRepository;
    private final LreExecutionHistoryRepository executionHistoryRepository;
    private final LreApiClient lreApiClient;

    public LreServiceImpl(
            LreServerConfigRepository serverConfigRepository,
            LreSavedRunConfigRepository savedRunConfigRepository,
            LreExecutionHistoryRepository executionHistoryRepository,
            LreApiClient lreApiClient
    ) {
        this.serverConfigRepository = serverConfigRepository;
        this.savedRunConfigRepository = savedRunConfigRepository;
        this.executionHistoryRepository = executionHistoryRepository;
        this.lreApiClient = lreApiClient;
    }

    // --- Server Configurations ---

    @Override
    @Transactional(readOnly = true)
    public List<LreServerConfigDTO> getAllServerConfigs() {
        return serverConfigRepository.findAll().stream()
                .map(this::toServerDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LreServerConfigDTO> getActiveServerConfigs() {
        return serverConfigRepository.findByActiveTrue().stream()
                .map(this::toServerDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LreServerConfigDTO> getServerConfigById(Long id) {
        return serverConfigRepository.findById(id).map(this::toServerDTO);
    }

    @Override
    public LreServerConfigDTO saveServerConfig(LreServerConfigDTO dto) {
        LreServerConfigEntity entity;
        if (dto.getId() != null) {
            entity = serverConfigRepository.findById(dto.getId())
                    .orElse(new LreServerConfigEntity());
        } else {
            entity = new LreServerConfigEntity();
        }

        entity.setName(dto.getName());
        entity.setHost(dto.getHost());
        entity.setPort(dto.getPort() > 0 ? dto.getPort() : (dto.isUseHttps() ? 443 : 80));
        entity.setUseHttps(dto.isUseHttps());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setClientId(dto.getClientId());
        entity.setClientSecret(dto.getClientSecret());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.isActive());

        LreServerConfigEntity saved = serverConfigRepository.save(entity);
        return toServerDTO(saved);
    }

    @Override
    public void deleteServerConfig(Long id) {
        serverConfigRepository.deleteById(id);
    }

    @Override
    public boolean testConnection(Long serverConfigId) {
        LreServerConfigEntity server = serverConfigRepository.findById(serverConfigId)
                .orElseThrow(() -> new IllegalArgumentException("LRE Server Config not found: " + serverConfigId));
        return lreApiClient.authenticate(server);
    }

    // --- LRE Metadata Exploration (JSON based) ---

    @Override
    public List<LreDomainDTO> getDomains(Long serverConfigId) {
        LreServerConfigEntity server = serverConfigRepository.findById(serverConfigId)
                .orElseThrow(() -> new IllegalArgumentException("LRE Server Config not found: " + serverConfigId));
        return lreApiClient.getDomains(server);
    }

    @Override
    public List<LreProjectDTO> getProjects(Long serverConfigId, String domainName) {
        LreServerConfigEntity server = serverConfigRepository.findById(serverConfigId)
                .orElseThrow(() -> new IllegalArgumentException("LRE Server Config not found: " + serverConfigId));
        return lreApiClient.getProjects(server, domainName);
    }

    @Override
    public List<LreTestDTO> getTests(Long serverConfigId, String domainName, String projectName) {
        LreServerConfigEntity server = serverConfigRepository.findById(serverConfigId)
                .orElseThrow(() -> new IllegalArgumentException("LRE Server Config not found: " + serverConfigId));
        return lreApiClient.getTests(server, domainName, projectName);
    }

    // --- Saved Profiles ---

    @Override
    @Transactional(readOnly = true)
    public List<LreSavedRunConfigDTO> getAllSavedProfiles() {
        return savedRunConfigRepository.findAll().stream()
                .map(this::toProfileDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LreSavedRunConfigDTO> getSavedProfilesByServer(Long serverConfigId) {
        return savedRunConfigRepository.findByServerConfigId(serverConfigId).stream()
                .map(this::toProfileDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LreSavedRunConfigDTO> getSavedProfileById(Long id) {
        return savedRunConfigRepository.findById(id).map(this::toProfileDTO);
    }

    @Override
    public LreSavedRunConfigDTO saveProfile(LreSavedRunConfigDTO dto) {
        LreServerConfigEntity server = serverConfigRepository.findById(dto.getServerConfigId())
                .orElseThrow(() -> new IllegalArgumentException("Server config not found: " + dto.getServerConfigId()));

        LreSavedRunConfigEntity entity;
        if (dto.getId() != null) {
            entity = savedRunConfigRepository.findById(dto.getId())
                    .orElse(new LreSavedRunConfigEntity());
        } else {
            entity = new LreSavedRunConfigEntity();
        }

        entity.setProfileName(dto.getProfileName());
        entity.setServerConfig(server);
        entity.setDomainName(dto.getDomainName());
        entity.setProjectName(dto.getProjectName());
        entity.setTestId(dto.getTestId());
        entity.setTestName(dto.getTestName());
        entity.setTestInstanceId(dto.getTestInstanceId());
        entity.setTimeslotId(dto.getTimeslotId());
        entity.setDurationInMinutes(dto.getDurationInMinutes());
        entity.setVusers(dto.getVusers());
        entity.setPostRunAction(dto.getPostRunAction());
        entity.setDescription(dto.getDescription());

        LreSavedRunConfigEntity saved = savedRunConfigRepository.save(entity);
        return toProfileDTO(saved);
    }

    @Override
    public void deleteProfile(Long id) {
        savedRunConfigRepository.deleteById(id);
    }

    // --- Async Execution ---

    @Override
    @Async
    public CompletableFuture<LreExecutionHistoryDTO> executeTestAsync(
            Long serverConfigId,
            String domainName,
            String projectName,
            LreRunRequestDTO request,
            String testName,
            Long savedConfigId
    ) {
        LreServerConfigEntity server = serverConfigRepository.findById(serverConfigId)
                .orElseThrow(() -> new IllegalArgumentException("LRE Server Config not found: " + serverConfigId));

        LreSavedRunConfigEntity savedConfig = null;
        if (savedConfigId != null) {
            savedConfig = savedRunConfigRepository.findById(savedConfigId).orElse(null);
        }

        // Create Execution History Record
        LreExecutionHistoryEntity history = new LreExecutionHistoryEntity(
                savedConfig,
                server,
                domainName,
                projectName,
                request.getTestId(),
                testName,
                request.getVusersNum(),
                request.getDurationInMinutes(),
                request.getTimeslotId(),
                request.getPostRunAction()
        );
        history = executionHistoryRepository.save(history);

        try {
            log.info("Triggering async LRE test execution on server '{}' (Domain: {}, Project: {}, Test ID: {})",
                    server.getName(), domainName, projectName, request.getTestId());

            LreRunResponseDTO response = lreApiClient.startTestRun(server, domainName, projectName, request);

            history.setRunId(response.getRunId());
            history.setStatus(response.getStatus() != null ? response.getStatus() : "RUNNING");
            history.setExecutionMessage("Run dispatched successfully to LRE. Run ID: " + response.getRunId());

            if (savedConfig != null) {
                savedConfig.setLastExecutedAt(LocalDateTime.now());
                savedConfig.setLastExecutionStatus(history.getStatus());
                savedRunConfigRepository.save(savedConfig);
            }

            executionHistoryRepository.save(history);
            return CompletableFuture.completedFuture(toHistoryDTO(history));

        } catch (Exception e) {
            log.error("Failed to execute LRE test run asynchronously", e);
            history.setStatus("FAILED");
            history.setExecutionMessage("Error triggering LRE test run: " + e.getMessage());
            history.setCompletedAt(LocalDateTime.now());

            if (savedConfig != null) {
                savedConfig.setLastExecutedAt(LocalDateTime.now());
                savedConfig.setLastExecutionStatus("FAILED");
                savedRunConfigRepository.save(savedConfig);
            }

            executionHistoryRepository.save(history);
            return CompletableFuture.completedFuture(toHistoryDTO(history));
        }
    }

    @Override
    @Async
    public CompletableFuture<LreExecutionHistoryDTO> executeSavedProfileAsync(Long savedProfileId) {
        LreSavedRunConfigEntity profile = savedRunConfigRepository.findById(savedProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Saved profile not found: " + savedProfileId));

        LreRunRequestDTO request = new LreRunRequestDTO(
                profile.getTestId(),
                profile.getTestInstanceId(),
                profile.getTimeslotId(),
                profile.getVusers(),
                profile.getDurationInMinutes(),
                profile.getPostRunAction()
        );

        return executeTestAsync(
                profile.getServerConfig().getId(),
                profile.getDomainName(),
                profile.getProjectName(),
                request,
                profile.getTestName(),
                profile.getId()
        );
    }

    // --- Execution History & Status ---

    @Override
    @Transactional(readOnly = true)
    public List<LreExecutionHistoryDTO> getRecentExecutions() {
        return executionHistoryRepository.findTop50ByOrderByInitiatedAtDesc().stream()
                .map(this::toHistoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LreExecutionHistoryDTO> getExecutionById(Long id) {
        return executionHistoryRepository.findById(id).map(this::toHistoryDTO);
    }

    @Override
    public LreExecutionHistoryDTO refreshExecutionStatus(Long executionHistoryId) {
        LreExecutionHistoryEntity history = executionHistoryRepository.findById(executionHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("Execution history not found: " + executionHistoryId));

        if (history.getRunId() != null && !"FINISHED".equalsIgnoreCase(history.getStatus()) && !"FAILED".equalsIgnoreCase(history.getStatus())) {
            try {
                LreRunResponseDTO statusResponse = lreApiClient.getRunStatus(
                        history.getServerConfig(),
                        history.getDomainName(),
                        history.getProjectName(),
                        history.getRunId()
                );

                if (statusResponse.getStatus() != null) {
                    history.setStatus(statusResponse.getStatus());
                    if ("FINISHED".equalsIgnoreCase(statusResponse.getStatus()) || "STOPPED".equalsIgnoreCase(statusResponse.getStatus()) || "FAILED".equalsIgnoreCase(statusResponse.getStatus())) {
                        history.setCompletedAt(LocalDateTime.now());
                    }
                    executionHistoryRepository.save(history);
                }
            } catch (Exception e) {
                log.warn("Failed to refresh status for run ID {}", history.getRunId(), e);
            }
        }

        return toHistoryDTO(history);
    }

    // --- Mapping Helpers ---

    private LreServerConfigDTO toServerDTO(LreServerConfigEntity e) {
        return new LreServerConfigDTO(
                e.getId(),
                e.getName(),
                e.getHost(),
                e.getPort(),
                e.isUseHttps(),
                e.getUsername(),
                e.getPassword(),
                e.getClientId(),
                e.getClientSecret(),
                e.getDescription(),
                e.isActive()
        );
    }

    private LreSavedRunConfigDTO toProfileDTO(LreSavedRunConfigEntity e) {
        LreSavedRunConfigDTO dto = new LreSavedRunConfigDTO();
        dto.setId(e.getId());
        dto.setProfileName(e.getProfileName());
        dto.setServerConfigId(e.getServerConfig() != null ? e.getServerConfig().getId() : null);
        dto.setServerConfigName(e.getServerConfig() != null ? e.getServerConfig().getName() : null);
        dto.setServerBaseUrl(e.getServerConfig() != null ? e.getServerConfig().getBaseUrl() : null);
        dto.setDomainName(e.getDomainName());
        dto.setProjectName(e.getProjectName());
        dto.setTestId(e.getTestId());
        dto.setTestName(e.getTestName());
        dto.setTestInstanceId(e.getTestInstanceId());
        dto.setTimeslotId(e.getTimeslotId());
        dto.setDurationInMinutes(e.getDurationInMinutes());
        dto.setVusers(e.getVusers());
        dto.setPostRunAction(e.getPostRunAction());
        dto.setDescription(e.getDescription());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setLastExecutedAt(e.getLastExecutedAt());
        dto.setLastExecutionStatus(e.getLastExecutionStatus());
        return dto;
    }

    private LreExecutionHistoryDTO toHistoryDTO(LreExecutionHistoryEntity e) {
        LreExecutionHistoryDTO dto = new LreExecutionHistoryDTO();
        dto.setId(e.getId());
        dto.setSavedConfigId(e.getSavedConfig() != null ? e.getSavedConfig().getId() : null);
        dto.setSavedProfileName(e.getSavedConfig() != null ? e.getSavedConfig().getProfileName() : null);
        dto.setServerConfigId(e.getServerConfig() != null ? e.getServerConfig().getId() : null);
        dto.setServerConfigName(e.getServerConfig() != null ? e.getServerConfig().getName() : null);
        dto.setDomainName(e.getDomainName());
        dto.setProjectName(e.getProjectName());
        dto.setTestId(e.getTestId());
        dto.setTestName(e.getTestName());
        dto.setRunId(e.getRunId());
        dto.setStatus(e.getStatus());
        dto.setVusers(e.getVusers());
        dto.setDurationInMinutes(e.getDurationInMinutes());
        dto.setTimeslotId(e.getTimeslotId());
        dto.setPostRunAction(e.getPostRunAction());
        dto.setExecutionMessage(e.getExecutionMessage());
        dto.setInitiatedAt(e.getInitiatedAt());
        dto.setCompletedAt(e.getCompletedAt());
        return dto;
    }
}