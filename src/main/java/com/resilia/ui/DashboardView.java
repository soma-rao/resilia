package com.resilia.ui;

import com.resilia.dto.DomainDTO;
import com.resilia.dto.ProjectDTO;
import com.resilia.dto.RunDTO;
import com.resilia.service.PerformanceCenterService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Dashboard | Resilia Performance Center")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    private final PerformanceCenterService service;

    public DashboardView(PerformanceCenterService service) {
        this.service = service;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader(), createMetricsRow(), createRecentRunsSection());
    }

    private Component createHeader() {
        VerticalLayout headerLayout = new VerticalLayout();
        headerLayout.setPadding(false);
        headerLayout.setSpacing(false);

        H2 title = new H2("Performance Center Dashboard");
        Span subtitle = new Span("Enterprise performance testing and infrastructure overview");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        headerLayout.add(title, subtitle);
        return headerLayout;
    }

    private Component createMetricsRow() {
        HorizontalLayout metricsLayout = new HorizontalLayout();
        metricsLayout.setWidthFull();
        metricsLayout.setSpacing(true);

        List<DomainDTO> domains = service.getAllDomains();
        int totalDomains = domains.size();
        int totalProjects = 0;
        int totalTests = 0;
        int totalGenerators = 0;
        List<RunDTO> allRuns = new ArrayList<>();

        for (DomainDTO domain : domains) {
            List<ProjectDTO> projects = service.getProjectsByDomain(domain.getName());
            totalProjects += projects.size();
            for (ProjectDTO project : projects) {
                totalTests += service.getTests(domain.getName(), project.getName()).size();
                totalGenerators += service.getLoadGenerators(domain.getName(), project.getName()).size();
                allRuns.addAll(service.getRuns(domain.getName(), project.getName(), null));
            }
        }

        long activeRuns = allRuns.stream()
                .filter(r -> "RUNNING".equalsIgnoreCase(r.getStatus()) || "INITIALIZING".equalsIgnoreCase(r.getStatus()))
                .count();

        metricsLayout.add(
                createCard("Domains", String.valueOf(totalDomains), VaadinIcon.BUILDING, "var(--lumo-primary-color)"),
                createCard("Projects", String.valueOf(totalProjects), VaadinIcon.FOLDER_OPEN, "var(--lumo-success-color)"),
                createCard("Performance Tests", String.valueOf(totalTests), VaadinIcon.FLASK, "var(--lumo-contrast-70pct)"),
                createCard("Active Runs", String.valueOf(activeRuns), VaadinIcon.PLAY_CIRCLE, "var(--lumo-error-color)"),
                createCard("Load Generators", String.valueOf(totalGenerators), VaadinIcon.SERVER, "var(--lumo-primary-color-50pct)")
        );

        return metricsLayout;
    }

    private Component createCard(String title, String value, VaadinIcon iconType, String iconColor) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.Border.ALL,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Padding.MEDIUM
        );
        card.setSpacing(false);

        Icon icon = iconType.create();
        icon.setSize("24px");
        icon.getStyle().set("color", iconColor);

        Span titleSpan = new Span(title);
        titleSpan.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL, LumoUtility.FontWeight.MEDIUM);

        H3 valueHeader = new H3(value);
        valueHeader.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontSize.XLARGE);

        HorizontalLayout top = new HorizontalLayout(icon, titleSpan);
        top.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        card.add(top, valueHeader);
        return card;
    }

    private Component createRecentRunsSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);

        H3 sectionTitle = new H3("Recent Test Executions");
        sectionTitle.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.Margin.Bottom.XSMALL);

        Grid<RunDTO> grid = new Grid<>(RunDTO.class, false);
        grid.addColumn(RunDTO::getId).setHeader("Run ID").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(RunDTO::getTestName).setHeader("Test Name").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(RunDTO::getDomain).setHeader("Domain").setAutoWidth(true);
        grid.addColumn(RunDTO::getProject).setHeader("Project").setAutoWidth(true);
        grid.addColumn(RunDTO::getVusers).setHeader("Vusers").setAutoWidth(true);
        grid.addColumn(RunDTO::getDurationInMinutes).setHeader("Duration (min)").setAutoWidth(true);
        grid.addComponentColumn(this::createStatusBadge).setHeader("Status").setAutoWidth(true);
        grid.addColumn(RunDTO::getStartTime).setHeader("Start Time").setAutoWidth(true);
        grid.addColumn(RunDTO::getPostRunAction).setHeader("Post Run Action").setAutoWidth(true);

        List<RunDTO> allRuns = new ArrayList<>();
        for (DomainDTO domain : service.getAllDomains()) {
            for (ProjectDTO project : service.getProjectsByDomain(domain.getName())) {
                allRuns.addAll(service.getRuns(domain.getName(), project.getName(), null));
            }
        }
        grid.setItems(allRuns);

        layout.add(sectionTitle, grid);
        return layout;
    }

    private Component createStatusBadge(RunDTO run) {
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
}
