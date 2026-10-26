package com.resilia.config;

import com.resilia.dto.*;
import com.resilia.lre.dto.LreSavedRunConfigDTO;
import com.resilia.lre.dto.LreServerConfigDTO;
import com.resilia.lre.service.LreService;
import com.resilia.service.PerformanceCenterService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(PerformanceCenterService service, LreService lreService) {
        return args -> {
            if (service.getAllDomains().isEmpty()) {
                // Initialize default domain
                DomainDTO defaultDomain = new DomainDTO();
                defaultDomain.setName("DEFAULT");
                defaultDomain.setDescription("Default Enterprise Domain");
                service.createDomain(defaultDomain);

                DomainDTO financeDomain = new DomainDTO();
                financeDomain.setName("FINANCE");
                financeDomain.setDescription("Financial Systems & Banking Applications");
                service.createDomain(financeDomain);

                // Initialize projects
                ProjectDTO portalProj = new ProjectDTO();
                portalProj.setName("CustomerPortal");
                portalProj.setDescription("Online Banking and Customer Web Portal");
                portalProj.setActive(true);
                service.createProject("DEFAULT", portalProj);

                ProjectDTO coreBankingProj = new ProjectDTO();
                coreBankingProj.setName("CoreBanking");
                coreBankingProj.setDescription("High-throughput transaction processing engine");
                coreBankingProj.setActive(true);
                service.createProject("FINANCE", coreBankingProj);

                // Initialize Load Generators
                LoadGeneratorDTO lg1 = new LoadGeneratorDTO();
                lg1.setName("LG-US-EAST-01");
                lg1.setHost("10.0.1.101");
                lg1.setPort(443);
                lg1.setStatus("Operational");
                lg1.setCapacity(5000);
                lg1.setLocation("US East (N. Virginia)");
                service.registerLoadGenerator("DEFAULT", "CustomerPortal", lg1);

                LoadGeneratorDTO lg2 = new LoadGeneratorDTO();
                lg2.setName("LG-EU-WEST-01");
                lg2.setHost("10.0.2.201");
                lg2.setPort(443);
                lg2.setStatus("Operational");
                lg2.setCapacity(10000);
                lg2.setLocation("EU West (Frankfurt)");
                service.registerLoadGenerator("FINANCE", "CoreBanking", lg2);

                // Initialize Tests
                TestCreateDTO test1 = new TestCreateDTO();
                test1.setName("Login & Account Summary Peak Load");
                test1.setType("Web-HTTP/HTML");
                test1.setDescription("Simulates 2500 concurrent users logging in and checking account dashboard");
                test1.setVusers(2500);
                test1.setDurationInMinutes(60);
                test1.setFolderPath("Subject/Regression/Sprint42");
                TestDTO createdTest1 = service.createTest("DEFAULT", "CustomerPortal", test1);

                TestCreateDTO test2 = new TestCreateDTO();
                test2.setName("High Volume Fund Transfer Stress Test");
                test2.setType("Web-Services-REST");
                test2.setDescription("Stress testing the money transfer API endpoints up to breaking point");
                test2.setVusers(5000);
                test2.setDurationInMinutes(120);
                test2.setFolderPath("Subject/Stress/Payments");
                TestDTO createdTest2 = service.createTest("FINANCE", "CoreBanking", test2);

                // Initialize Test Sets
                TestSetDTO testSet1 = new TestSetDTO();
                testSet1.setName("Sprint 42 Release Baseline");
                testSet1.setDescription("Pre-deployment load testing suite for Sprint 42");
                testSet1.setFolderPath("Root/Releases/2026/Q4");
                testSet1.setTestIds(java.util.List.of(createdTest1.getId()));
                service.createTestSet("DEFAULT", "CustomerPortal", testSet1);

                // Initialize Timeslots
                TimeslotDTO timeslot = new TimeslotDTO();
                timeslot.setName("Q4 Performance Window");
                timeslot.setStartTime(LocalDateTime.now().minusHours(2));
                timeslot.setEndTime(LocalDateTime.now().plusHours(6));
                timeslot.setVusersAllocated(3000);
                timeslot.setStatus("RESERVED");
                TimeslotDTO savedTimeslot = service.reserveTimeslot("DEFAULT", "CustomerPortal", timeslot);

                // Initialize Test Run
                RunCreateRequestDTO runRequest = new RunCreateRequestDTO();
                runRequest.setTestId(createdTest1.getId());
                runRequest.setTimeslotId(savedTimeslot.getId());
                runRequest.setDurationInMinutes(60);
                runRequest.setVusers(2500);
                runRequest.setPostRunAction("Collate And Analyze");
                RunDTO run = service.startRun("DEFAULT", "CustomerPortal", runRequest);
            }

            // Initialize LRE Server Configs if empty
            if (lreService.getAllServerConfigs().isEmpty()) {
                LreServerConfigDTO localServer = new LreServerConfigDTO();
                localServer.setName("Local Performance Center");
                localServer.setHost("localhost");
                localServer.setPort(8080);
                localServer.setUseHttps(false);
                localServer.setUsername("admin");
                localServer.setPassword("Admin123!");
                localServer.setDescription("Local Resilia / LRE mock environment endpoint");
                localServer.setActive(true);
                LreServerConfigDTO savedServer = lreService.saveServerConfig(localServer);

                LreServerConfigDTO prodServer = new LreServerConfigDTO();
                prodServer.setName("LRE Production Cluster");
                prodServer.setHost("lre-prod.enterprise.corp");
                prodServer.setPort(443);
                prodServer.setUseHttps(true);
                prodServer.setUsername("svc_perf_ci");
                prodServer.setPassword("S3cur3P@ssw0rd");
                prodServer.setClientId("perf-automation-client");
                prodServer.setClientSecret("sec_8f902a7b31c9");
                prodServer.setDescription("Primary LoadRunner Enterprise production cluster");
                prodServer.setActive(true);
                lreService.saveServerConfig(prodServer);

                // Initialize sample saved run profile
                LreSavedRunConfigDTO profile = new LreSavedRunConfigDTO();
                profile.setProfileName("Nightly_CustomerPortal_Regression");
                profile.setServerConfigId(savedServer.getId());
                profile.setDomainName("DEFAULT");
                profile.setProjectName("CustomerPortal");
                profile.setTestId(1L);
                profile.setTestName("Login & Account Summary Peak Load");
                profile.setVusers(2500);
                profile.setDurationInMinutes(60);
                profile.setPostRunAction("Collate And Analyze");
                profile.setDescription("Automated nightly regression suite profile");
                lreService.saveProfile(profile);
            }
        };
    }
}
