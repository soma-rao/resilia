package com.resilia.lre;

import com.resilia.lre.dto.*;
import com.resilia.lre.service.LreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LreServiceTest {

    @Autowired
    private LreService lreService;

    @Test
    void testServerConfigAndProfileLifecycle() {
        // 1. Create Server Config
        LreServerConfigDTO server = new LreServerConfigDTO();
        server.setName("QA-LRE-Cluster");
        server.setHost("lre-qa.corp.internal");
        server.setPort(443);
        server.setUseHttps(true);
        server.setUsername("qa_runner");
        server.setPassword("Secret123!");
        server.setDescription("QA LoadRunner Server");
        server.setActive(true);

        LreServerConfigDTO savedServer = lreService.saveServerConfig(server);
        assertNotNull(savedServer.getId());
        assertEquals("QA-LRE-Cluster", savedServer.getName());
        assertEquals("https://lre-qa.corp.internal:443", savedServer.getBaseUrl());

        // 2. Query Server Config
        Optional<LreServerConfigDTO> retrieved = lreService.getServerConfigById(savedServer.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("qa_runner", retrieved.get().getUsername());

        // 3. Save Execution Profile
        LreSavedRunConfigDTO profile = new LreSavedRunConfigDTO();
        profile.setProfileName("Smoke_Test_Profile");
        profile.setServerConfigId(savedServer.getId());
        profile.setDomainName("DEFAULT");
        profile.setProjectName("CustomerPortal");
        profile.setTestId(101L);
        profile.setTestName("Smoke Test 101");
        profile.setVusers(200);
        profile.setDurationInMinutes(15);
        profile.setPostRunAction("Collate And Analyze");
        profile.setDescription("Automated smoke run");

        LreSavedRunConfigDTO savedProfile = lreService.saveProfile(profile);
        assertNotNull(savedProfile.getId());
        assertEquals("Smoke_Test_Profile", savedProfile.getProfileName());

        // 4. Query Profiles
        List<LreSavedRunConfigDTO> profiles = lreService.getSavedProfilesByServer(savedServer.getId());
        assertFalse(profiles.isEmpty());
        assertTrue(profiles.stream().anyMatch(p -> p.getProfileName().equals("Smoke_Test_Profile")));

        // 5. Test Async Execution Record Creation
        LreRunRequestDTO runRequest = new LreRunRequestDTO(101L, null, null, 200, 15, "Collate And Analyze");
        CompletableFuture<LreExecutionHistoryDTO> asyncExec = lreService.executeTestAsync(
                savedServer.getId(),
                "DEFAULT",
                "CustomerPortal",
                runRequest,
                "Smoke Test 101",
                savedProfile.getId()
        );

        LreExecutionHistoryDTO history = asyncExec.join();
        assertNotNull(history.getId());
        assertEquals("DEFAULT", history.getDomainName());
        assertEquals("CustomerPortal", history.getProjectName());
        assertEquals(101L, history.getTestId());
        assertNotNull(history.getStatus());

        // 6. Verify Execution History
        List<LreExecutionHistoryDTO> recentLogs = lreService.getRecentExecutions();
        assertFalse(recentLogs.isEmpty());
        assertTrue(recentLogs.stream().anyMatch(h -> h.getId().equals(history.getId())));
    }
}