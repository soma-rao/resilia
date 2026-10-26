package com.resilia;

import com.resilia.dto.*;
import com.resilia.service.PerformanceCenterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PerformanceCenterServiceTest {

    @Autowired
    private PerformanceCenterService service;

    @Test
    void testPerformanceCenterWorkflow() {
        try {
            // 1. Create Domain
            System.out.println("[DEBUG_LOG] Starting test");
            DomainDTO domain = new DomainDTO(null, "TEST_DOMAIN", "Test Domain");
            DomainDTO createdDomain = service.createDomain(domain);
            System.out.println("[DEBUG_LOG] Created domain: " + createdDomain.getId());
            assertNotNull(createdDomain.getId());
            assertEquals("TEST_DOMAIN", createdDomain.getName());

            // 2. Create Project
            ProjectDTO project = new ProjectDTO(null, "BankingApp", "TEST_DOMAIN", "Banking performance project");
            ProjectDTO createdProject = service.createProject("TEST_DOMAIN", project);
            System.out.println("[DEBUG_LOG] Created project: " + createdProject.getId());
            assertNotNull(createdProject.getId());

            // 3. Create Test
            TestCreateDTO testCreate = new TestCreateDTO(
                    "LoginLoadTest",
                    "Performance",
                    "Subject\\LoadTests",
                    "Test login peak load",
                    30,
                    150
            );
            TestDTO createdTest = service.createTest("TEST_DOMAIN", "BankingApp", testCreate);
            System.out.println("[DEBUG_LOG] Created test: " + createdTest.getId());
            assertNotNull(createdTest.getId());
            assertEquals("LoginLoadTest", createdTest.getName());

            // 4. Reserve Timeslot
            TimeslotDTO timeslot = new TimeslotDTO(null, "PeakSlot", "TEST_DOMAIN", "BankingApp", null, 60, 200, "RESERVED");
            TimeslotDTO reservedTimeslot = service.reserveTimeslot("TEST_DOMAIN", "BankingApp", timeslot);
            System.out.println("[DEBUG_LOG] Created timeslot: " + reservedTimeslot.getId());
            assertNotNull(reservedTimeslot.getId());

            // 5. Start Test Run
            RunCreateRequestDTO runReq = new RunCreateRequestDTO(createdTest.getId(), null, reservedTimeslot.getId(), "COLLATE_AND_ANALYZE");
            RunDTO startedRun = service.startRun("TEST_DOMAIN", "BankingApp", runReq);
            System.out.println("[DEBUG_LOG] Started run: " + startedRun.getId() + " status: " + startedRun.getStatus());
            assertNotNull(startedRun.getId());
            assertEquals("RUNNING", startedRun.getStatus());

            // 6. Stop Run
            Optional<RunDTO> stoppedRun = service.stopRun("TEST_DOMAIN", "BankingApp", startedRun.getId());
            System.out.println("[DEBUG_LOG] Stopped run present: " + stoppedRun.isPresent());
            assertTrue(stoppedRun.isPresent());
            assertEquals("FINISHED", stoppedRun.get().getStatus());

            // 7. Check Run Results
            List<RunResultDTO> results = service.getRunResults("TEST_DOMAIN", "BankingApp", startedRun.getId());
            System.out.println("[DEBUG_LOG] Run results size: " + results.size());
            assertFalse(results.isEmpty());
            assertEquals("RAW_RESULTS", results.get(0).getType());

            // 8. Register Load Generator
            LoadGeneratorDTO lg = new LoadGeneratorDTO(null, "LG_Node_01", "TEST_DOMAIN", "BankingApp", "lg01.corp", "10.0.0.12", "OPERATIONAL", "US-East");
            LoadGeneratorDTO createdLg = service.registerLoadGenerator("TEST_DOMAIN", "BankingApp", lg);
            System.out.println("[DEBUG_LOG] Created LG: " + createdLg.getId());
            assertNotNull(createdLg.getId());
        } catch (Throwable t) {
            System.out.println("[DEBUG_LOG] Exception caught: " + t.getMessage());
            t.printStackTrace(System.out);
            throw t;
        }
    }
}
