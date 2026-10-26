package com.resilia.service;

import com.resilia.dto.*;
import java.util.List;
import java.util.Optional;

public interface PerformanceCenterService {
    // Domains & Projects
    List<DomainDTO> getAllDomains();
    DomainDTO createDomain(DomainDTO domainDTO);
    List<ProjectDTO> getProjectsByDomain(String domain);
    ProjectDTO createProject(String domain, ProjectDTO projectDTO);

    // Tests
    List<TestDTO> getTests(String domain, String project);
    Optional<TestDTO> getTestById(String domain, String project, Long testId);
    TestDTO createTest(String domain, String project, TestCreateDTO testCreateDTO);
    boolean deleteTest(String domain, String project, Long testId);

    // Test Sets
    List<TestSetDTO> getTestSets(String domain, String project);
    TestSetDTO createTestSet(String domain, String project, TestSetDTO testSetDTO);

    // Test Runs
    List<RunDTO> getRuns(String domain, String project, String status);
    Optional<RunDTO> getRunById(String domain, String project, Long runId);
    RunDTO startRun(String domain, String project, RunCreateRequestDTO request);
    Optional<RunDTO> stopRun(String domain, String project, Long runId);
    List<RunResultDTO> getRunResults(String domain, String project, Long runId);

    // Load Generators
    List<LoadGeneratorDTO> getLoadGenerators(String domain, String project);
    LoadGeneratorDTO registerLoadGenerator(String domain, String project, LoadGeneratorDTO generatorDTO);

    // Timeslots
    List<TimeslotDTO> getTimeslots(String domain, String project);
    TimeslotDTO reserveTimeslot(String domain, String project, TimeslotDTO timeslotDTO);
}
