package com.resilia.service;

import com.resilia.dto.*;
import com.resilia.entity.*;
import com.resilia.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PerformanceCenterServiceImpl implements PerformanceCenterService {

    private final DomainRepository domainRepository;
    private final ProjectRepository projectRepository;
    private final TestRepository testRepository;
    private final TestSetRepository testSetRepository;
    private final RunRepository runRepository;
    private final RunResultRepository runResultRepository;
    private final LoadGeneratorRepository loadGeneratorRepository;
    private final TimeslotRepository timeslotRepository;

    public PerformanceCenterServiceImpl(
            DomainRepository domainRepository,
            ProjectRepository projectRepository,
            TestRepository testRepository,
            TestSetRepository testSetRepository,
            RunRepository runRepository,
            RunResultRepository runResultRepository,
            LoadGeneratorRepository loadGeneratorRepository,
            TimeslotRepository timeslotRepository) {
        this.domainRepository = domainRepository;
        this.projectRepository = projectRepository;
        this.testRepository = testRepository;
        this.testSetRepository = testSetRepository;
        this.runRepository = runRepository;
        this.runResultRepository = runResultRepository;
        this.loadGeneratorRepository = loadGeneratorRepository;
        this.timeslotRepository = timeslotRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainDTO> getAllDomains() {
        return domainRepository.findAll().stream()
                .map(d -> new DomainDTO(d.getId(), d.getName(), d.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public DomainDTO createDomain(DomainDTO domainDTO) {
        DomainEntity entity = new DomainEntity(domainDTO.getName(), domainDTO.getDescription());
        DomainEntity saved = domainRepository.save(entity);
        return new DomainDTO(saved.getId(), saved.getName(), saved.getDescription());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByDomain(String domain) {
        return projectRepository.findByDomainName(domain).stream()
                .map(p -> new ProjectDTO(p.getId(), p.getName(), p.getDomainName(), p.getDescription(), p.isActive()))
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDTO createProject(String domain, ProjectDTO projectDTO) {
        ProjectEntity entity = new ProjectEntity(projectDTO.getName(), domain, projectDTO.getDescription(), projectDTO.isActive());
        ProjectEntity saved = projectRepository.save(entity);
        return new ProjectDTO(saved.getId(), saved.getName(), saved.getDomainName(), saved.getDescription(), saved.isActive());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestDTO> getTests(String domain, String project) {
        return testRepository.findByDomainAndProject(domain, project).stream()
                .map(this::toTestDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TestDTO> getTestById(String domain, String project, Long testId) {
        return testRepository.findByIdAndDomainAndProject(testId, domain, project)
                .map(this::toTestDTO);
    }

    @Override
    public TestDTO createTest(String domain, String project, TestCreateDTO testCreateDTO) {
        TestEntity entity = new TestEntity(
                null,
                testCreateDTO.getName(),
                domain,
                project,
                testCreateDTO.getTestType(),
                testCreateDTO.getTestFolder(),
                testCreateDTO.getDescription(),
                testCreateDTO.getDurationMinutes(),
                testCreateDTO.getVusers()
        );
        TestEntity saved = testRepository.save(entity);
        return toTestDTO(saved);
    }

    @Override
    public boolean deleteTest(String domain, String project, Long testId) {
        Optional<TestEntity> test = testRepository.findByIdAndDomainAndProject(testId, domain, project);
        if (test.isPresent()) {
            testRepository.delete(test.get());
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestSetDTO> getTestSets(String domain, String project) {
        return testSetRepository.findByDomainAndProject(domain, project).stream()
                .map(ts -> new TestSetDTO(ts.getId(), ts.getName(), ts.getDomain(), ts.getProject(), ts.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public TestSetDTO createTestSet(String domain, String project, TestSetDTO testSetDTO) {
        TestSetEntity entity = new TestSetEntity(null, testSetDTO.getName(), domain, project, testSetDTO.getDescription());
        TestSetEntity saved = testSetRepository.save(entity);
        return new TestSetDTO(saved.getId(), saved.getName(), saved.getDomain(), saved.getProject(), saved.getDescription());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RunDTO> getRuns(String domain, String project, String status) {
        List<RunEntity> runs = (status != null && !status.isEmpty())
                ? runRepository.findByDomainAndProjectAndStatus(domain, project, status)
                : runRepository.findByDomainAndProject(domain, project);

        return runs.stream()
                .map(this::toRunDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RunDTO> getRunById(String domain, String project, Long runId) {
        return runRepository.findByIdAndDomainAndProject(runId, domain, project)
                .map(this::toRunDTO);
    }

    @Override
    public RunDTO startRun(String domain, String project, RunCreateRequestDTO request) {
        String testName = "Test #" + request.getTestId();
        int vusers = request.getVusers() != null ? request.getVusers() : 100;
        int durationSec = request.getDurationInMinutes() != null ? request.getDurationInMinutes() * 60 : 0;
        Optional<TestEntity> testEntity = testRepository.findByIdAndDomainAndProject(request.getTestId(), domain, project);
        if (testEntity.isPresent()) {
            testName = testEntity.get().getName();
            if (request.getVusers() == null && testEntity.get().getVusers() != null) {
                vusers = testEntity.get().getVusers();
            }
            if (request.getDurationInMinutes() == null && testEntity.get().getDurationMinutes() != null) {
                durationSec = testEntity.get().getDurationMinutes() * 60;
            }
        }

        RunEntity runEntity = new RunEntity(
                null,
                request.getTestId(),
                testName,
                domain,
                project,
                "RUNNING",
                LocalDateTime.now(),
                null,
                durationSec,
                vusers,
                request.getTimeslotId(),
                request.getPostRunAction()
        );
        RunEntity saved = runRepository.save(runEntity);
        return toRunDTO(saved);
    }

    @Override
    public Optional<RunDTO> stopRun(String domain, String project, Long runId) {
        Optional<RunEntity> optionalRun = runRepository.findByIdAndDomainAndProject(runId, domain, project);
        if (optionalRun.isPresent()) {
            RunEntity run = optionalRun.get();
            run.setStatus("FINISHED");
            run.setEndTime(LocalDateTime.now());
            if (run.getStartTime() != null) {
                run.setDurationSeconds((int) java.time.Duration.between(run.getStartTime(), run.getEndTime()).toSeconds());
            }
            RunEntity updated = runRepository.save(run);

            // Add dummy raw results report
            RunResultEntity resultEntity = new RunResultEntity(
                    updated.getId(),
                    "Results_Run_" + updated.getId() + ".zip",
                    "RAW_RESULTS",
                    "/rest/domains/" + domain + "/projects/" + project + "/runs/" + updated.getId() + "/results/download",
                    1024000L
            );
            runResultRepository.save(resultEntity);

            return Optional.of(toRunDTO(updated));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RunResultDTO> getRunResults(String domain, String project, Long runId) {
        return runResultRepository.findByRunId(runId).stream()
                .map(r -> new RunResultDTO(r.getId(), r.getRunId(), r.getName(), r.getType(), r.getFileUrl(), r.getFileSizeBytes()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoadGeneratorDTO> getLoadGenerators(String domain, String project) {
        return loadGeneratorRepository.findByDomainAndProject(domain, project).stream()
                .map(this::toLoadGeneratorDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LoadGeneratorDTO registerLoadGenerator(String domain, String project, LoadGeneratorDTO dto) {
        LoadGeneratorEntity entity = new LoadGeneratorEntity(
                dto.getName(),
                domain,
                project,
                dto.getHostName(),
                dto.getIpAddress(),
                dto.getStatus() != null ? dto.getStatus() : "OPERATIONAL",
                dto.getLocation(),
                dto.getPort() != null ? dto.getPort() : 443,
                dto.getCapacity() != null ? dto.getCapacity() : 1000
        );
        LoadGeneratorEntity saved = loadGeneratorRepository.save(entity);
        return toLoadGeneratorDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeslotDTO> getTimeslots(String domain, String project) {
        return timeslotRepository.findByDomainAndProject(domain, project).stream()
                .map(this::toTimeslotDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TimeslotDTO reserveTimeslot(String domain, String project, TimeslotDTO dto) {
        TimeslotEntity entity = new TimeslotEntity(
                dto.getName(),
                domain,
                project,
                dto.getStartTime() != null ? dto.getStartTime() : LocalDateTime.now(),
                dto.getDurationMinutes() != null ? dto.getDurationMinutes() : 60,
                dto.getVusersCount() != null ? dto.getVusersCount() : 50,
                dto.getStatus() != null ? dto.getStatus() : "RESERVED"
        );
        TimeslotEntity saved = timeslotRepository.save(entity);
        return toTimeslotDTO(saved);
    }

    private TestDTO toTestDTO(TestEntity e) {
        return new TestDTO(
                e.getId(),
                e.getName(),
                e.getDomain(),
                e.getProject(),
                e.getTestType(),
                e.getTestFolder(),
                e.getDescription(),
                e.getDurationMinutes(),
                e.getVusers()
        );
    }

    private RunDTO toRunDTO(RunEntity e) {
        return new RunDTO(
                e.getId(),
                e.getTestId(),
                e.getTestName(),
                e.getDomain(),
                e.getProject(),
                e.getStatus(),
                e.getStartTime(),
                e.getEndTime(),
                e.getDurationSeconds(),
                e.getVusersCount(),
                e.getTimeslotId(),
                e.getPostRunAction()
        );
    }

    private LoadGeneratorDTO toLoadGeneratorDTO(LoadGeneratorEntity e) {
        return new LoadGeneratorDTO(
                e.getId(),
                e.getName(),
                e.getDomain(),
                e.getProject(),
                e.getHostName(),
                e.getIpAddress(),
                e.getStatus(),
                e.getLocation(),
                e.getPort(),
                e.getCapacity()
        );
    }

    private TimeslotDTO toTimeslotDTO(TimeslotEntity e) {
        return new TimeslotDTO(
                e.getId(),
                e.getName(),
                e.getDomain(),
                e.getProject(),
                e.getStartTime(),
                e.getDurationMinutes(),
                e.getVusersCount(),
                e.getStatus()
        );
    }
}
