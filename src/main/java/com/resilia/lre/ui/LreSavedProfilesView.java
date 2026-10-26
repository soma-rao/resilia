package com.resilia.lre.ui;

import com.resilia.lre.dto.LreExecutionHistoryDTO;
import com.resilia.lre.dto.LreSavedRunConfigDTO;
import com.resilia.lre.service.LreService;
import com.resilia.ui.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

@PageTitle("Saved Test Profiles | Resilia Performance Center")
@Route(value = "lre/profiles", layout = MainLayout.class)
public class LreSavedProfilesView extends VerticalLayout {

    private final LreService lreService;
    private final Grid<LreSavedRunConfigDTO> profileGrid = new Grid<>(LreSavedRunConfigDTO.class, false);
    private final Grid<LreExecutionHistoryDTO> historyGrid = new Grid<>(LreExecutionHistoryDTO.class, false);

    private final VerticalLayout profilesTabContent = new VerticalLayout();
    private final VerticalLayout historyTabContent = new VerticalLayout();

    public LreSavedProfilesView(LreService lreService) {
        this.lreService = lreService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader(), createTabsAndContent());
        configureProfileGrid();
        configureHistoryGrid();
        refreshProfiles();
        refreshHistory();
    }

    private VerticalLayout createHeader() {
        VerticalLayout headerLayout = new VerticalLayout();
        headerLayout.setPadding(false);
        headerLayout.setSpacing(false);

        H2 title = new H2("Saved LRE Execution Profiles");
        Span subtitle = new Span("Manage automated run templates and trigger 1-click re-executions without human intervention");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        headerLayout.add(title, subtitle);
        return headerLayout;
    }

    private VerticalLayout createTabsAndContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);

        Tab profilesTab = new Tab(VaadinIcon.AUTOMATION.create(), new Span("Saved Run Profiles (Automated Re-Run)"));
        Tab historyTab = new Tab(VaadinIcon.TIME_FORWARD.create(), new Span("Execution Audit Log"));

        Tabs tabs = new Tabs(profilesTab, historyTab);
        tabs.setWidthFull();

        profilesTabContent.setSizeFull();
        profilesTabContent.setPadding(false);

        HorizontalLayout profileToolbar = new HorizontalLayout();
        profileToolbar.setWidthFull();
        profileToolbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        H3 profileTitle = new H3("Persisted Run Configurations");
        profileTitle.addClassNames(LumoUtility.Margin.NONE);

        Button refreshProfileBtn = new Button("Refresh Profiles", VaadinIcon.REFRESH.create(), e -> refreshProfiles());
        refreshProfileBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        profileToolbar.add(profileTitle, refreshProfileBtn);
        profileToolbar.expand(profileTitle);

        profilesTabContent.add(profileToolbar, profileGrid);

        historyTabContent.setSizeFull();
        historyTabContent.setPadding(false);
        historyTabContent.setVisible(false);

        HorizontalLayout historyToolbar = new HorizontalLayout();
        historyToolbar.setWidthFull();
        historyToolbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        H3 historyTitle = new H3("All Historical Test Executions");
        historyTitle.addClassNames(LumoUtility.Margin.NONE);

        Button refreshHistoryBtn = new Button("Refresh Logs", VaadinIcon.REFRESH.create(), e -> refreshHistory());
        refreshHistoryBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        historyToolbar.add(historyTitle, refreshHistoryBtn);
        historyToolbar.expand(historyTitle);

        historyTabContent.add(historyToolbar, historyGrid);

        tabs.addSelectedChangeListener(event -> {
            boolean isProfiles = event.getSelectedTab().equals(profilesTab);
            profilesTabContent.setVisible(isProfiles);
            historyTabContent.setVisible(!isProfiles);
            if (isProfiles) {
                refreshProfiles();
            } else {
                refreshHistory();
            }
        });

        layout.add(tabs, profilesTabContent, historyTabContent);
        return layout;
    }

    private void configureProfileGrid() {
        profileGrid.setSizeFull();
        profileGrid.addColumn(LreSavedRunConfigDTO::getProfileName).setHeader("Profile Name").setAutoWidth(true).setFlexGrow(1);
        profileGrid.addColumn(LreSavedRunConfigDTO::getServerConfigName).setHeader("LRE Host").setAutoWidth(true);
        profileGrid.addColumn(LreSavedRunConfigDTO::getDomainName).setHeader("Domain").setAutoWidth(true);
        profileGrid.addColumn(LreSavedRunConfigDTO::getProjectName).setHeader("Project").setAutoWidth(true);
        profileGrid.addColumn(LreSavedRunConfigDTO::getTestName).setHeader("Test Name").setAutoWidth(true).setFlexGrow(1);
        profileGrid.addColumn(LreSavedRunConfigDTO::getVusers).setHeader("Vusers").setAutoWidth(true);
        profileGrid.addColumn(LreSavedRunConfigDTO::getDurationInMinutes).setHeader("Duration (min)").setAutoWidth(true);

        profileGrid.addComponentColumn(p -> {
            String status = p.getLastExecutionStatus() != null ? p.getLastExecutionStatus() : "NOT RUN";
            Span badge = new Span(status);
            if (status.equalsIgnoreCase("RUNNING")) {
                badge.getElement().getThemeList().add("badge primary pill");
            } else if (status.equalsIgnoreCase("FINISHED") || status.equalsIgnoreCase("PASSED")) {
                badge.getElement().getThemeList().add("badge success pill");
            } else if (status.equalsIgnoreCase("FAILED")) {
                badge.getElement().getThemeList().add("badge error pill");
            } else {
                badge.getElement().getThemeList().add("badge contrast pill");
            }
            return badge;
        }).setHeader("Last Status").setAutoWidth(true);

        profileGrid.addColumn(LreSavedRunConfigDTO::getLastExecutedAt).setHeader("Last Executed").setAutoWidth(true);

        profileGrid.addComponentColumn(this::createProfileActions).setHeader("Actions").setAutoWidth(true).setFlexGrow(1);
    }

    private HorizontalLayout createProfileActions(LreSavedRunConfigDTO profile) {
        Button runBtn = new Button("Run Now", VaadinIcon.PLAY.create(), e -> executeSavedProfile(profile));
        runBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);

        Button deleteBtn = new Button(VaadinIcon.TRASH.create(), e -> deleteProfile(profile));
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(runBtn, deleteBtn);
        actions.setSpacing(true);
        return actions;
    }

    private void executeSavedProfile(LreSavedRunConfigDTO profile) {
        UI ui = UI.getCurrent();
        Notification.show("Dispatching automated execution for profile: '" + profile.getProfileName() + "'...", 3000, Notification.Position.TOP_CENTER);

        lreService.executeSavedProfileAsync(profile.getId())
                .thenAccept(history -> {
                    if (ui != null) {
                        ui.access(() -> {
                            refreshProfiles();
                            Notification.show("Profile '" + profile.getProfileName() + "' dispatched! Run ID: " + (history.getRunId() != null ? history.getRunId() : "Pending"), 4000, Notification.Position.TOP_CENTER)
                                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        });
                    }
                }).exceptionally(ex -> {
                    if (ui != null) {
                        ui.access(() -> {
                            refreshProfiles();
                            Notification.show("Profile execution failed: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        });
                    }
                    return null;
                });
    }

    private void deleteProfile(LreSavedRunConfigDTO profile) {
        lreService.deleteProfile(profile.getId());
        refreshProfiles();
        Notification.show("Saved profile removed", 2500, Notification.Position.BOTTOM_START);
    }

    private void configureHistoryGrid() {
        historyGrid.setSizeFull();
        historyGrid.addColumn(LreExecutionHistoryDTO::getId).setHeader("Log ID").setAutoWidth(true).setFlexGrow(0);
        historyGrid.addColumn(h -> h.getRunId() != null ? String.valueOf(h.getRunId()) : "-").setHeader("LRE Run ID").setAutoWidth(true);
        historyGrid.addColumn(LreExecutionHistoryDTO::getSavedProfileName).setHeader("Profile Name").setAutoWidth(true);
        historyGrid.addColumn(LreExecutionHistoryDTO::getServerConfigName).setHeader("LRE Host").setAutoWidth(true);
        historyGrid.addColumn(LreExecutionHistoryDTO::getDomainName).setHeader("Domain").setAutoWidth(true);
        historyGrid.addColumn(LreExecutionHistoryDTO::getProjectName).setHeader("Project").setAutoWidth(true);
        historyGrid.addColumn(LreExecutionHistoryDTO::getTestName).setHeader("Test Name").setAutoWidth(true).setFlexGrow(1);
        historyGrid.addColumn(LreExecutionHistoryDTO::getVusers).setHeader("Vusers").setAutoWidth(true);

        historyGrid.addComponentColumn(h -> {
            String status = h.getStatus() != null ? h.getStatus() : "UNKNOWN";
            Span badge = new Span(status);
            if (status.equalsIgnoreCase("RUNNING") || status.equalsIgnoreCase("INITIALIZING")) {
                badge.getElement().getThemeList().add("badge primary pill");
            } else if (status.equalsIgnoreCase("FINISHED") || status.equalsIgnoreCase("PASSED")) {
                badge.getElement().getThemeList().add("badge success pill");
            } else if (status.equalsIgnoreCase("FAILED")) {
                badge.getElement().getThemeList().add("badge error pill");
            } else {
                badge.getElement().getThemeList().add("badge contrast pill");
            }
            return badge;
        }).setHeader("Status").setAutoWidth(true);

        historyGrid.addColumn(LreExecutionHistoryDTO::getInitiatedAt).setHeader("Initiated At").setAutoWidth(true);
        historyGrid.addColumn(LreExecutionHistoryDTO::getCompletedAt).setHeader("Completed At").setAutoWidth(true);

        historyGrid.addComponentColumn(h -> {
            Button detailBtn = new Button(VaadinIcon.INFO_CIRCLE.create(), e -> showHistoryDetails(h));
            detailBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            return detailBtn;
        }).setHeader("Details").setAutoWidth(true);
    }

    private void showHistoryDetails(LreExecutionHistoryDTO history) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Execution Log #" + history.getId());
        dialog.setWidth("500px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        content.add(new Paragraph("Status: " + history.getStatus()));
        content.add(new Paragraph("Run ID: " + (history.getRunId() != null ? history.getRunId() : "N/A")));
        content.add(new Paragraph("Message: " + (history.getExecutionMessage() != null ? history.getExecutionMessage() : "None")));
        content.add(new Paragraph("Initiated: " + history.getInitiatedAt()));
        if (history.getCompletedAt() != null) {
            content.add(new Paragraph("Completed: " + history.getCompletedAt()));
        }

        Button closeBtn = new Button("Close", e -> dialog.close());
        dialog.add(content);
        dialog.getFooter().add(closeBtn);
        dialog.open();
    }

    private void refreshProfiles() {
        List<LreSavedRunConfigDTO> list = lreService.getAllSavedProfiles();
        profileGrid.setItems(list);
    }

    private void refreshHistory() {
        List<LreExecutionHistoryDTO> list = lreService.getRecentExecutions();
        historyGrid.setItems(list);
    }
}