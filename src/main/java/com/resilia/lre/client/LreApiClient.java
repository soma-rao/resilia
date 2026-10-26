package com.resilia.lre.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resilia.lre.dto.*;
import com.resilia.lre.entity.LreServerConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LreApiClient {

    private static final Logger log = LoggerFactory.getLogger(LreApiClient.class);
    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;

    // Cache of session cookies keyed by serverConfig.id
    private final Map<Long, List<String>> sessionCookies = new ConcurrentHashMap<>();

    public LreApiClient(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.restClientBuilder = restClientBuilder;
        this.objectMapper = objectMapper;
    }

    /**
     * Authenticates with LRE using JSON or Basic Auth depending on server configuration.
     * Reference: https://admhelp.microfocus.com/lre/en/all/api_refs/Performance_Center_REST_API/Content/authenticate.htm
     */
    public boolean authenticate(LreServerConfigEntity serverConfig) {
        String baseUrl = serverConfig.getBaseUrl();
        RestClient client = restClientBuilder.baseUrl(baseUrl).build();

        try {
            // 1. If ClientID / ClientSecret is provided, try OAuth endpoint
            if (serverConfig.getClientId() != null && !serverConfig.getClientId().isBlank()) {
                LreAuthRequestDTO authDto = new LreAuthRequestDTO(serverConfig.getClientId(), serverConfig.getClientSecret(), true);
                ResponseEntity<String> response = client.post()
                        .uri("/LoadTest/rest/authentication-point/oauth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(authDto)
                        .retrieve()
                        .toEntity(String.class);

                storeCookies(serverConfig.getId(), response.getHeaders());
                return response.getStatusCode().is2xxSuccessful();
            }

            // 2. Otherwise try standard JSON authentication
            if (serverConfig.getUsername() != null && !serverConfig.getUsername().isBlank()) {
                String authHeader = "Basic " + Base64.getEncoder().encodeToString(
                        (serverConfig.getUsername() + ":" + (serverConfig.getPassword() != null ? serverConfig.getPassword() : "")).getBytes(StandardCharsets.UTF_8)
                );

                ResponseEntity<String> response = client.get()
                        .uri("/LoadTest/rest/authentication-point/authenticate")
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(String.class);

                storeCookies(serverConfig.getId(), response.getHeaders());
                return response.getStatusCode().is2xxSuccessful();
            }

            return false;
        } catch (Exception e) {
            log.error("Authentication failed for LRE server: {}", serverConfig.getName(), e);
            throw new RuntimeException("Authentication failed for LRE Server '" + serverConfig.getName() + "': " + e.getMessage(), e);
        }
    }

    /**
     * Fetch all Domains from LRE in JSON format.
     * Reference: https://admhelp.microfocus.com/lre/en/all/api_refs/Performance_Center_REST_API/Content/Get-Domains.htm
     */
    public List<LreDomainDTO> getDomains(LreServerConfigEntity serverConfig) {
        ensureAuthenticated(serverConfig);
        RestClient client = restClientBuilder.baseUrl(serverConfig.getBaseUrl()).build();

        try {
            String json = client.get()
                    .uri("/LoadTest/rest/domains")
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(h -> applyCookies(serverConfig.getId(), h))
                    .retrieve()
                    .body(String.class);

            if (json == null || json.isBlank()) {
                return Collections.emptyList();
            }

            return parseDomainList(json);
        } catch (Exception e) {
            log.error("Failed to fetch domains from LRE server {}", serverConfig.getName(), e);
            throw new RuntimeException("Failed to fetch domains: " + e.getMessage(), e);
        }
    }

    /**
     * Fetch Projects for a Domain from LRE in JSON format.
     * Reference: https://admhelp.microfocus.com/lre/en/all/api_refs/Performance_Center_REST_API/Content/Get-Projects.htm
     */
    public List<LreProjectDTO> getProjects(LreServerConfigEntity serverConfig, String domain) {
        ensureAuthenticated(serverConfig);
        RestClient client = restClientBuilder.baseUrl(serverConfig.getBaseUrl()).build();

        try {
            String json = client.get()
                    .uri("/LoadTest/rest/domains/{domain}/projects", domain)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(h -> applyCookies(serverConfig.getId(), h))
                    .retrieve()
                    .body(String.class);

            if (json == null || json.isBlank()) {
                return Collections.emptyList();
            }

            return parseProjectList(json, domain);
        } catch (Exception e) {
            log.error("Failed to fetch projects for domain {} from LRE server {}", domain, serverConfig.getName(), e);
            throw new RuntimeException("Failed to fetch projects for domain '" + domain + "': " + e.getMessage(), e);
        }
    }

    /**
     * Fetch Tests for a Domain/Project from LRE in JSON format.
     * Reference: https://admhelp.microfocus.com/lre/en/all/api_refs/Performance_Center_REST_API/Content/tests-get-all.htm
     */
    public List<LreTestDTO> getTests(LreServerConfigEntity serverConfig, String domain, String project) {
        ensureAuthenticated(serverConfig);
        RestClient client = restClientBuilder.baseUrl(serverConfig.getBaseUrl()).build();

        try {
            String json = client.get()
                    .uri("/LoadTest/rest/domains/{domain}/projects/{project}/tests", domain, project)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(h -> applyCookies(serverConfig.getId(), h))
                    .retrieve()
                    .body(String.class);

            if (json == null || json.isBlank()) {
                return Collections.emptyList();
            }

            return parseTestList(json);
        } catch (Exception e) {
            log.error("Failed to fetch tests for project {} in domain {} from LRE server {}", project, domain, serverConfig.getName(), e);
            throw new RuntimeException("Failed to fetch tests for '" + domain + "/" + project + "': " + e.getMessage(), e);
        }
    }

    /**
     * Start a Test Run on LRE in JSON format.
     * Reference: https://admhelp.microfocus.com/lre/en/all/api_refs/Performance_Center_REST_API/Content/start_test_run.htm
     */
    public LreRunResponseDTO startTestRun(LreServerConfigEntity serverConfig, String domain, String project, LreRunRequestDTO request) {
        ensureAuthenticated(serverConfig);
        RestClient client = restClientBuilder.baseUrl(serverConfig.getBaseUrl()).build();

        try {
            ResponseEntity<String> response = client.post()
                    .uri("/LoadTest/rest/domains/{domain}/projects/{project}/Runs", domain, project)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(h -> applyCookies(serverConfig.getId(), h))
                    .body(request)
                    .retrieve()
                    .toEntity(String.class);

            String body = response.getBody();
            if (body != null && !body.isBlank()) {
                JsonNode root = objectMapper.readTree(body);
                LreRunResponseDTO res = new LreRunResponseDTO();
                if (root.has("RunID")) {
                    res.setRunId(root.get("RunID").asLong());
                } else if (root.has("ID")) {
                    res.setRunId(root.get("ID").asLong());
                } else if (root.has("id")) {
                    res.setRunId(root.get("id").asLong());
                }
                if (root.has("Status")) {
                    res.setStatus(root.get("Status").asText());
                } else if (root.has("status")) {
                    res.setStatus(root.get("status").asText());
                } else {
                    res.setStatus("RUNNING");
                }
                return res;
            }

            return new LreRunResponseDTO(null, "ACCEPTED");
        } catch (Exception e) {
            log.error("Failed to start test run on LRE server {} (Domain: {}, Project: {}, Test: {})",
                    serverConfig.getName(), domain, project, request.getTestId(), e);
            throw new RuntimeException("Failed to start LRE test run: " + e.getMessage(), e);
        }
    }

    /**
     * Get run status from LRE in JSON format.
     */
    public LreRunResponseDTO getRunStatus(LreServerConfigEntity serverConfig, String domain, String project, Long runId) {
        ensureAuthenticated(serverConfig);
        RestClient client = restClientBuilder.baseUrl(serverConfig.getBaseUrl()).build();

        try {
            String json = client.get()
                    .uri("/LoadTest/rest/domains/{domain}/projects/{project}/Runs/{runId}", domain, project, runId)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(h -> applyCookies(serverConfig.getId(), h))
                    .retrieve()
                    .body(String.class);

            if (json != null && !json.isBlank()) {
                JsonNode root = objectMapper.readTree(json);
                LreRunResponseDTO res = new LreRunResponseDTO();
                res.setRunId(runId);
                if (root.has("Status")) {
                    res.setStatus(root.get("Status").asText());
                } else if (root.has("status")) {
                    res.setStatus(root.get("status").asText());
                }
                return res;
            }
            return new LreRunResponseDTO(runId, "UNKNOWN");
        } catch (Exception e) {
            log.warn("Could not query run status for run ID {} on server {}", runId, serverConfig.getName(), e);
            return new LreRunResponseDTO(runId, "RUNNING");
        }
    }

    private void ensureAuthenticated(LreServerConfigEntity serverConfig) {
        if (!sessionCookies.containsKey(serverConfig.getId())) {
            authenticate(serverConfig);
        }
    }

    private void storeCookies(Long serverId, HttpHeaders headers) {
        List<String> setCookieList = headers.get(HttpHeaders.SET_COOKIE);
        if (setCookieList != null && !setCookieList.isEmpty()) {
            sessionCookies.put(serverId, new ArrayList<>(setCookieList));
        }
    }

    private void applyCookies(Long serverId, HttpHeaders headers) {
        List<String> cookies = sessionCookies.get(serverId);
        if (cookies != null && !cookies.isEmpty()) {
            for (String cookie : cookies) {
                // Keep name=value part before semicolon
                String cookieVal = cookie.split(";")[0];
                headers.add(HttpHeaders.COOKIE, cookieVal);
            }
        }
    }

    private List<LreDomainDTO> parseDomainList(String json) {
        List<LreDomainDTO> list = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode domainsNode = root.has("Domain") ? root.get("Domain") :
                    (root.has("domains") ? root.get("domains") : (root.isArray() ? root : null));

            if (domainsNode != null && domainsNode.isArray()) {
                for (JsonNode node : domainsNode) {
                    String name = node.has("Name") ? node.get("Name").asText() :
                            (node.has("name") ? node.get("name").asText() : "");
                    String desc = node.has("Description") ? node.get("Description").asText() :
                            (node.has("description") ? node.get("description").asText() : "");
                    if (!name.isBlank()) {
                        list.add(new LreDomainDTO(name, desc));
                    }
                }
            } else if (domainsNode != null && domainsNode.isObject()) {
                String name = domainsNode.has("Name") ? domainsNode.get("Name").asText() : "";
                String desc = domainsNode.has("Description") ? domainsNode.get("Description").asText() : "";
                if (!name.isBlank()) {
                    list.add(new LreDomainDTO(name, desc));
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse LRE domain JSON", e);
        }
        return list;
    }

    private List<LreProjectDTO> parseProjectList(String json, String domain) {
        List<LreProjectDTO> list = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode projectsNode = root.has("Project") ? root.get("Project") :
                    (root.has("projects") ? root.get("projects") : (root.isArray() ? root : null));

            if (projectsNode != null && projectsNode.isArray()) {
                for (JsonNode node : projectsNode) {
                    String name = node.has("Name") ? node.get("Name").asText() :
                            (node.has("name") ? node.get("name").asText() : "");
                    String desc = node.has("Description") ? node.get("Description").asText() :
                            (node.has("description") ? node.get("description").asText() : "");
                    if (!name.isBlank()) {
                        list.add(new LreProjectDTO(name, desc, domain));
                    }
                }
            } else if (projectsNode != null && projectsNode.isObject()) {
                String name = projectsNode.has("Name") ? projectsNode.get("Name").asText() : "";
                String desc = projectsNode.has("Description") ? projectsNode.get("Description").asText() : "";
                if (!name.isBlank()) {
                    list.add(new LreProjectDTO(name, desc, domain));
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse LRE project JSON", e);
        }
        return list;
    }

    private List<LreTestDTO> parseTestList(String json) {
        List<LreTestDTO> list = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode testsNode = root.has("Test") ? root.get("Test") :
                    (root.has("tests") ? root.get("tests") : (root.isArray() ? root : null));

            if (testsNode != null && testsNode.isArray()) {
                for (JsonNode node : testsNode) {
                    list.add(parseSingleTest(node));
                }
            } else if (testsNode != null && testsNode.isObject()) {
                list.add(parseSingleTest(testsNode));
            }
        } catch (Exception e) {
            log.error("Failed to parse LRE test JSON", e);
        }
        return list;
    }

    private LreTestDTO parseSingleTest(JsonNode node) {
        Long id = node.has("ID") ? node.get("ID").asLong() : (node.has("id") ? node.get("id").asLong() : null);
        String name = node.has("Name") ? node.get("Name").asText() : (node.has("name") ? node.get("name").asText() : "");
        String type = node.has("TestType") ? node.get("TestType").asText() : (node.has("testType") ? node.get("testType").asText() : "");
        String folder = node.has("TestFolder") ? node.get("TestFolder").asText() : (node.has("testFolder") ? node.get("testFolder").asText() : "");
        String desc = node.has("Description") ? node.get("Description").asText() : (node.has("description") ? node.get("description").asText() : "");
        Integer vusers = node.has("Vusers") ? node.get("Vusers").asInt() : (node.has("vusers") ? node.get("vusers").asInt() : 100);
        Integer duration = node.has("Duration") ? node.get("Duration").asInt() : (node.has("duration") ? node.get("duration").asInt() : 30);

        return new LreTestDTO(id, name, type, folder, desc, vusers, duration);
    }
}