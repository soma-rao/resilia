package com.resilia.ui;

import com.resilia.dto.DomainDTO;
import com.resilia.dto.ProjectDTO;
import com.resilia.dto.RunDTO;
import com.resilia.dto.RunResultDTO;
import com.resilia.service.PerformanceCenterService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Collections;
import java.util.List;

@PageTitle("Test Runs | Resilia Performance Center")
@Route(value = "runs", layout = MainLayout.class)
public class RunsView extends VerticalLayout {

    private final PerformanceCenterService service;

    private final ComboBox<DomainDTO> domainSelect = new ComboBox<>("Domain");
    private final ComboBox<ProjectDTO> projectSelect = new ComboBox<>("Project");
    private final ComboBox<String> statusFilter = new ComboBox<>("Status Filter");

    private final Grid<RunDTO> runGrid = new Grid<>(RunDTO.class, false);

    public RunsView(PerformanceCenterService service) {
        this.service = service;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader(), createFilterToolbar(), createGrid());
        initSelectors();
    }

    private VerticalLayout createHeader() {
        VerticalLayout headerLayout = new VerticalLayout();
        headerLayout.setPadding(false);
        headerLayout.setSpacing(false);

        H2 title = new H2("Test Execution Runs");
        Span subtitle = new Span("Monitor active runs, inspect historical results, and manage lifecycle");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        headerLayout.add(title, subtitle);
        return headerLayout;
    }

    private HorizontalLayout createFilterToolbar() {
        domainSelect.setItemLabelGenerator(DomainDTO::getName);
        projectSelect.setItemLabelGenerator(ProjectDTO::getName);

        domainSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                List<ProjectDTO> projects = service.getProjectsByDomain(e.getValue().getName());
                projectSelect.setItems(projects);
                if (!projects.isEmpty()) {
                    projectSelect.setValue(projects.get(0));
                } else {
                    projectSelect.clear();
                    refreshRuns();
                }
            } else {
                projectSelect.clear();
                projectSelect.setItems(Collections.emptyList());
                refreshRuns();
            }
        });

        projectSelect.addValueChangeListener(e -> refreshRuns());

        statusFilter.setItems("ALL", "RUNNING", "PASSED", "FAILED", "INITIALIZING");
        statusFilter.setValue("ALL");
        statusFilter.addValueChangeListener(e -> refreshRuns());

        Button refreshBtn = new Button("Refresh", VaadinIcon.REFRESH.create(), e -> refreshRuns());

        HorizontalLayout toolbar = new HorizontalLayout(domainSelect, projectSelect, statusFilter, refreshBtn);
        toolbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        return toolbar;
    }

    private void initSelectors() {
        List<DomainDTO> domains = service.getAllDomains();
        domainSelect.setItems(domains);
        if (!domains.isEmpty()) {
            domainSelect.setValue(domains.get(0));
        }
    }

    private Grid<RunDTO> createGrid() {
        runGrid.setSizeFull();

        runGrid.addColumn(RunDTO::getId).setHeader("Run ID").setAutoWidth(true).setFlexGrow(0);
        runGrid.addColumn(RunDTO::getTestName).setHeader("Test Name").setAutoWidth(true).setFlexGrow(1);
        runGrid.addComponentColumn(this::createStatusBadge).setHeader("Status").setAutoWidth(true);
        runGrid.addColumn(RunDTO::getVusers).setHeader("Vusers").setAutoWidth(true);
        runGrid.addColumn(RunDTO::getDurationInMinutes).setHeader("Duration (min)").setAutoWidth(true);
        runGrid.addColumn(RunDTO::getStartTime).setHeader("Start Time").setAutoWidth(true);
        runGrid.addColumn(RunDTO::getEndTime).setHeader("End Time").setAutoWidth(true);
        runGrid.addColumn(RunDTO::getPostRunAction).setHeader("Post Action").setAutoWidth(true);

        runGrid.addComponentColumn(run -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button resultsBtn = new Button("Results", VaadinIcon.FILE_TEXT.create(), e -> openResultsDialog(run));
            resultsBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            actions.add(resultsBtn);

            if ("RUNNING".equalsIgnoreCase(run.getStatus()) || "INITIALIZING".equalsIgnoreCase(run.getStatus())) {
                Button stopBtn = new Button("Stop", VaadinIcon.STOP.create(), e -> {
                    if (domainSelect.getValue() != null && projectSelect.getValue() != null) {
                        service.stopRun(domainSelect.getValue().getName(), projectSelect.getValue().getName(), run.getId());
                        Notification.show("Run #" + run.getId() + " stopped", 3000, Notification.Position.TOP_CENTER)
                                .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                        refreshRuns();
                    }
                });
                stopBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
                actions.add(stopBtn);
            }

            return actions;
        }).setHeader("Actions").setAutoWidth(true);

        return runGrid;
    }

    private Span createStatusBadge(RunDTO run) {
        Span badge = new Span(run.getStatus() != null ? run.getStatus() : "UNKNOWN");
        String status = run.getStatus() != null ? run.getStatus().toUpperCase() : "";

        if (status.contains("RUNNING") || status.contains("INITIALIZING")) {
            badge.getElement().getThemeList().add("badge primary pill");
        } else if (status.contains("PASSED") || status.contains("FINISHED") || status.contains("COMPLETED")) {
            badge.getElement().getThemeList().add("badge success pill");
        } else if (status.contains("FAILED") || status.contains("ERROR")) {
            badge.getElement().getThemeList().add("badge error pill");
        } else {
            badge.getElement().getThemeList().add("badge contrast pill");
        }
        return badge;
    }

    private void refreshRuns() {
        DomainDTO domain = domainSelect.getValue();
        ProjectDTO project = projectSelect.getValue();
        String status = "ALL".equals(statusFilter.getValue()) ? null : statusFilter.getValue();

        if (domain != null && project != null) {
            runGrid.setItems(service.getRuns(domain.getName(), project.getName(), status));
        } else {
            runGrid.setItems(Collections.emptyList());
        }
    }

    private void openResultsDialog(RunDTO run) {
        if (domainSelect.getValue() == null || projectSelect.getValue() == null) return;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Run Results - #" + run.getId() + " (" + run.getTestName() + ")");
        dialog.setWidth("700px");

        List<RunResultDTO> results = service.getRunResults(
                domainSelect.getValue().getName(),
                projectSelect.getValue().getName(),
                run.getId()
        );

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);

        if (results.isEmpty()) {
            content.add(new Span("No result artifacts available for this run."));
        } else {
            Grid<RunResultDTO> resultGrid = new Grid<>(RunResultDTO.class, false);
            resultGrid.addColumn(RunResultDTO::getName).setHeader("Artifact Name").setAutoWidth(true).setFlexGrow(1);
            resultGrid.addColumn(RunResultDTO::getType).setHeader("Type").setAutoWidth(true);
            resultGrid.addColumn(r -> r.getFileSizeBytes() != null ? (r.getFileSizeBytes() / 1024) + " KB" : "-").setHeader("Size").setAutoWidth(true);
            resultGrid.addColumn(RunResultDTO::getCreatedTime).setHeader("Created").setAutoWidth(true);
            resultGrid.setItems(results);
            content.add(resultGrid);
        }

        dialog.add(content);

        Button closeBtn = new Button("Close", e -> dialog.close());
        dialog.getFooter().add(closeBtn);
        dialog.open();
    }
}
