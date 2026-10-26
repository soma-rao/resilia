package com.resilia.controller;

import com.resilia.dto.*;
import com.resilia.service.PerformanceCenterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class PerformanceCenterController {

    private final PerformanceCenterService service;

    public PerformanceCenterController(PerformanceCenterService service) {
        this.service = service;
    }

    // Authentication Endpoints
    @PostMapping("/LoadRunner/service-layer/authentication-point/authenticate")
    public ResponseEntity<Void> authenticate(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok()
                .header("Set-Cookie", "LWSSO_COOKIE_KEY=sample-token-123456; Path=/; HttpOnly")
                .build();
    }

    @GetMapping("/LoadRunner/service-layer/authentication-point/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    // Domains
    @GetMapping("/LoadRunner/service-layer/rest/domains")
    public ResponseEntity<List<DomainDTO>> getAllDomains() {
        return ResponseEntity.ok(service.getAllDomains());
    }

    @PostMapping("/LoadRunner/service-layer/rest/domains")
    public ResponseEntity<DomainDTO> createDomain(@RequestBody DomainDTO domainDTO) {
        DomainDTO created = service.createDomain(domainDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Projects
    @GetMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects")
    public ResponseEntity<List<ProjectDTO>> getProjectsByDomain(@PathVariable("domain") String domain) {
        return ResponseEntity.ok(service.getProjectsByDomain(domain));
    }

    @PostMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects")
    public ResponseEntity<ProjectDTO> createProject(
            @PathVariable("domain") String domain,
            @RequestBody ProjectDTO projectDTO) {
        ProjectDTO created = service.createProject(domain, projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Tests
    @GetMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/tests")
    public ResponseEntity<List<TestDTO>> getTests(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project) {
        return ResponseEntity.ok(service.getTests(domain, project));
    }

    @GetMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/tests/{testId}")
    public ResponseEntity<TestDTO> getTestById(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project,
            @PathVariable("testId") Long testId) {
        return service.getTestById(domain, project, testId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/tests")
    public ResponseEntity<TestDTO> createTest(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project,
            @RequestBody TestCreateDTO testCreateDTO) {
        TestDTO created = service.createTest(domain, project, testCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/tests/{testId}")
    public ResponseEntity<Void> deleteTest(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project,
            @PathVariable("testId") Long testId) {
        boolean deleted = service.deleteTest(domain, project, testId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Test Sets
    @GetMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/test-sets")
    public ResponseEntity<List<TestSetDTO>> getTestSets(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project) {
        return ResponseEntity.ok(service.getTestSets(domain, project));
    }

    @PostMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/test-sets")
    public ResponseEntity<TestSetDTO> createTestSet(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project,
            @RequestBody TestSetDTO testSetDTO) {
        TestSetDTO created = service.createTestSet(domain, project, testSetDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Test Runs
    @GetMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/runs")
    public ResponseEntity<List<RunDTO>> getRuns(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project,
            @RequestParam(name = "status", required = false) String status) {
        return ResponseEntity.ok(service.getRuns(domain, project, status));
    }

    @GetMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/runs/{runId}")
    public ResponseEntity<RunDTO> getRunById(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project,
            @PathVariable("runId") Long runId) {
        return service.getRunById(domain, project, runId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/runs")
    public ResponseEntity<RunDTO> startRun(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project,
            @RequestBody RunCreateRequestDTO request) {
        RunDTO created = service.startRun(domain, project, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/runs/{runId}/stop")
    public ResponseEntity<RunDTO> stopRun(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project,
            @PathVariable("runId") Long runId) {
        return service.stopRun(domain, project, runId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/runs/{runId}/results")
    public ResponseEntity<List<RunResultDTO>> getRunResults(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project,
            @PathVariable("runId") Long runId) {
        return ResponseEntity.ok(service.getRunResults(domain, project, runId));
    }

    // Load Generators
    @GetMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/load-generators")
    public ResponseEntity<List<LoadGeneratorDTO>> getLoadGenerators(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project) {
        return ResponseEntity.ok(service.getLoadGenerators(domain, project));
    }

    @PostMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/load-generators")
    public ResponseEntity<LoadGeneratorDTO> registerLoadGenerator(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project,
            @RequestBody LoadGeneratorDTO generatorDTO) {
        LoadGeneratorDTO created = service.registerLoadGenerator(domain, project, generatorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Timeslots
    @GetMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/timeslots")
    public ResponseEntity<List<TimeslotDTO>> getTimeslots(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project) {
        return ResponseEntity.ok(service.getTimeslots(domain, project));
    }

    @PostMapping("/LoadRunner/service-layer/rest/domains/{domain}/projects/{project}/timeslots")
    public ResponseEntity<TimeslotDTO> reserveTimeslot(
            @PathVariable("domain") String domain,
            @PathVariable("project") String project,
            @RequestBody TimeslotDTO timeslotDTO) {
        TimeslotDTO created = service.reserveTimeslot(domain, project, timeslotDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
