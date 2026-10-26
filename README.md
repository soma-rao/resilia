# Resilia

Resilia is a Vaadin Flow application built with Spring Boot, modeling Micro Focus / OpenText LoadRunner Enterprise (Performance Center) operations and test execution management.

## Features

- **Dashboard**: High-level metrics for domains, projects, tests, active runs, and load generators, alongside recent execution logs.
- **Domains & Projects Management**: Multi-tenant workspace organization for performance testing projects.
- **Tests & Scenarios Management**: Create, configure, run, and organize performance tests and test sets.
- **Test Runs & Monitoring**: Monitor live test executions, stop active runs, and inspect detailed result artifacts and collated reports.
- **Load Generators**: Register and monitor distributed load injection agent hosts.
- **Timeslots**: Reserve dedicated load testing windows and allocate virtual user capacities.
- **On-Demand LRE Test Runner**:
  - **LRE Host Configurations**: Configure multiple LoadRunner Enterprise host instances with host, port, protocol, Basic / OAuth credentials, and connection testing.
  - **Interactive On-Demand Wizard**: Select LRE Host &rarr; Select Domain &rarr; Select Project &rarr; Select Test &rarr; Configure Vusers, Duration, Timeslot, and Post-Run Action &rarr; Execute in asynchronous mode.
  - **Automated Profile Persistence**: Save test execution selections into a persistent database table for 1-click re-runs and unattended execution.
  - **Saved Profiles & Execution Audit**: Re-execute saved test profiles without human intervention and review historical audit logs.
- **REST & OpenAPI**: LoadRunner Enterprise compatible REST API with complete OpenAPI 3.0 specifications.

## Requirements

- Java 25 or newer
- Gradle 8.14 or newer

## Run locally

Start the development server with:

```shell
gradle bootRun
```

Then open [http://localhost:8080](http://localhost:8080).