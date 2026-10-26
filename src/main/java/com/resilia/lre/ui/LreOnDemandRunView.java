package com.resilia.lre.ui;

import com.resilia.lre.dto.*;
import com.resilia.lre.service.LreService;
import com.resilia.ui.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Collections;
import java.util.List;

@PageTitle("Run Test On Demand | Resilia Performance Center")
@Route(value = "lre/run", layout = MainLayout.class)
public class LreOnDemandRunView extends VerticalLayout {

    private final LreService lreService;

    // Cascade components
    private final ComboBox<LreServerConfigDTO> serverSelect = new ComboBox<>("1. Select LRE Host Configuration");
    private final ComboBox<LreDomainDTO> domainSelect = new ComboBox<>("2. Select Domain");
    private final ComboBox<LreProjectDTO> projectSelect = new ComboBox<>("3. Select Project");
    private final ComboBox<LreTestDTO> testSelect = new ComboBox<>("4. Select Performance Test");

    // Test parameters
    private final IntegerField vusersField = new IntegerField("Virtual Users (Vusers)");
    private final IntegerField durationField = new IntegerField("Duration (Minutes)");
    private final IntegerField timeslotIdField = new IntegerField("Timeslot ID (Optional)");
    private final Select<String> postRunActionSelect = new Select<>();

    // Save profile controls
    private final Checkbox saveProfileCheckbox = new Checkbox("Save this selection to run profile table for future re-execution without human intervention");
    private final TextField profileNameField = new TextField("Profile Name");

    // Action button & Status section
    private final Button executeButton = new Button("Execute Test (Async Mode)", VaadinIcon.PLAY.create());
    private final ProgressBar executionProgressBar = new ProgressBar();
    private final Div statusCard = new Div();
    private final Span statusLabel = new Span();
    private final Paragraph statusDetails = new Paragraph();

    // Recent runs grid
    private final Grid<LreExecutionHistoryDTO> historyGrid = new Grid<>(LreExecutionHistoryDTO.class, false);

    public LreOnDemandRunView(LreService lreService) {
        this.lreService = lreService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader(), createWizardCard(), createRecentExecutionsSection());
        setupFormBindings();
        refreshHistory();
    }

    private VerticalLayout createHeader() {
        VerticalLayout headerLayout = new VerticalLayout();
        headerLayout.setPadding(false);
        headerLayout.setSpacing(false);

        H2 title = new H2("Run LoadRunner Performance Test On Demand");
        Span subtitle = new Span("Cascade through LRE Host -> Domains -> Projects -> Tests and trigger asynchronous executions");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        headerLayout.add(title, subtitle);
        return headerLayout;
    }

    private VerticalLayout createWizardCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.Border.ALL,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Padding.LARGE
        );
        card.setSpacing(true);

        H3 sectionTitle = new H3("Test Selection & Execution Parameters");
        sectionTitle.addClassNames(LumoUtility.Margin.NONE);

        // Step 1: Cascade selection row
        FormLayout cascadeLayout = new FormLayout();
        cascadeLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 4)
        );

        serverSelect.setItemLabelGenerator(LreServerConfigDTO::getName);
        serverSelect.setPlaceholder("Choose LRE Server...");
        serverSelect.setRequired(true);

        domainSelect.setItemLabelGenerator(LreDomainDTO::getName);
        domainSelect.setPlaceholder("Choose Domain...");
        domainSelect.setEnabled(false);

        projectSelect.setItemLabelGenerator(LreProjectDTO::getName);
        projectSelect.setPlaceholder("Choose Project...");
        projectSelect.setEnabled(false);

        testSelect.setItemLabelGenerator(LreTestDTO::toString);
        testSelect.setPlaceholder("Choose Test...");
        testSelect.setEnabled(false);

        cascadeLayout.add(serverSelect, domainSelect, projectSelect, testSelect);

        // Step 2: Parameters Form
        FormLayout paramsLayout = new FormLayout();
        paramsLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 4)
        );

        vusersField.setValue(100);
        vusersField.setMin(1);

        durationField.setValue(30);
        durationField.setMin(1);

        timeslotIdField.setPlaceholder("e.g. 101");

        postRunActionSelect.setLabel("Post-Run Action");
        postRunActionSelect.setItems("Collate And Analyze", "Collate Only", "Do Not Collate");
        postRunActionSelect.setValue("Collate And Analyze");

        paramsLayout.add(vusersField, durationField, timeslotIdField, postRunActionSelect);

        // Step 3: Save profile row
        VerticalLayout saveProfileLayout = new VerticalLayout();
        saveProfileLayout.setPadding(false);
        saveProfileLayout.setSpacing(true);

        profileNameField.setPlaceholder("e.g. Daily_Nightly_Checkout_Stress");
        profileNameField.setWidthFull();
        profileNameField.setVisible(false);

        saveProfileCheckbox.addValueChangeListener(e -> profileNameField.setVisible(Boolean.TRUE.equals(e.getValue())));

        saveProfileLayout.add(saveProfileCheckbox, profileNameField);

        // Step 4: Execute controls & Status section
        executeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        executeButton.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Padding.Vertical.SMALL);
        executeButton.addClickListener(e -> executeTestOnDemand());

        executionProgressBar.setIndeterminate(true);
        executionProgressBar.setVisible(false);

        statusCard.setVisible(false);
        statusCard.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Border.ALL,
                LumoUtility.BorderRadius.SMALL,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Width.FULL
        );
        statusLabel.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.MEDIUM);
        statusCard.add(statusLabel, statusDetails);

        card.add(sectionTitle, cascadeLayout, paramsLayout, saveProfileLayout, executeButton, executionProgressBar, statusCard);
        return card;
    }

    private void setupFormBindings() {
        // Load active servers
        List<LreServerConfigDTO> servers = lreService.getActiveServerConfigs();
        serverSelect.setItems(servers);

        // 1. Server Changed -> Fetch Domains
        serverSelect.addValueChangeListener(event -> {
            domainSelect.clear();
            domainSelect.setItems(Collections.emptyList());
            projectSelect.clear();
            projectSelect.setItems(Collections.emptyList());
            testSelect.clear();
            testSelect.setItems(Collections.emptyList());

            LreServerConfigDTO server = event.getValue();
            if (server != null) {
                try {
                    List<LreDomainDTO> domains = lreService.getDomains(server.getId());
                    domainSelect.setItems(domains);
                    domainSelect.setEnabled(true);
                } catch (Exception ex) {
                    Notification.show("Could not load domains: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    domainSelect.setEnabled(false);
                }
            } else {
                domainSelect.setEnabled(false);
            }
        });

        // 2. Domain Changed -> Fetch Projects
        domainSelect.addValueChangeListener(event -> {
            projectSelect.clear();
            projectSelect.setItems(Collections.emptyList());
            testSelect.clear();
            testSelect.setItems(Collections.emptyList());

            LreServerConfigDTO server = serverSelect.getValue();
            LreDomainDTO domain = event.getValue();
            if (server != null && domain != null) {
                try {
                    List<LreProjectDTO> projects = lreService.getProjects(server.getId(), domain.getName());
                    projectSelect.setItems(projects);
                    projectSelect.setEnabled(true);
                } catch (Exception ex) {
                    Notification.show("Could not load projects: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    projectSelect.setEnabled(false);
                }
            } else {
                projectSelect.setEnabled(false);
            }
        });

        // 3. Project Changed -> Fetch Tests
        projectSelect.addValueChangeListener(event -> {
            testSelect.clear();
            testSelect.setItems(Collections.emptyList());

            LreServerConfigDTO server = serverSelect.getValue();
            LreDomainDTO domain = domainSelect.getValue();
            LreProjectDTO project = event.getValue();

            if (server != null && domain != null && project != null) {
                try {
                    List<LreTestDTO> tests = lreService.getTests(server.getId(), domain.getName(), project.getName());
                    testSelect.setItems(tests);
                    testSelect.setEnabled(true);
                } catch (Exception ex) {
                    Notification.show("Could not load tests: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    testSelect.setEnabled(false);
                }
            } else {
                testSelect.setEnabled(false);
            }
        });

        // 4. Test Selected -> Autofill default vusers and duration
        testSelect.addValueChangeListener(event -> {
            LreTestDTO test = event.getValue();
            if (test != null) {
                if (test.getVusers() != null && test.getVusers() > 0) {
                    vusersField.setValue(test.getVusers());
                }
                if (test.getDurationInMinutes() != null && test.getDurationInMinutes() > 0) {
                    durationField.setValue(test.getDurationInMinutes());
                }
                if (profileNameField.isEmpty()) {
                    profileNameField.setValue("Profile_" + test.getName().replaceAll("\\s+", "_"));
                }
            }
        });
    }

    private void executeTestOnDemand() {
        LreServerConfigDTO server = serverSelect.getValue();
        LreDomainDTO domain = domainSelect.getValue();
        LreProjectDTO project = projectSelect.getValue();
        LreTestDTO test = testSelect.getValue();

        if (server == null || domain == null || project == null || test == null) {
            Notification.show("Please select LRE Host, Domain, Project, and Performance Test", 3500, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        executeButton.setEnabled(false);
        executionProgressBar.setVisible(true);
        statusCard.setVisible(true);
        statusLabel.setText("Submitting asynchronous test execution to LRE server...");
        statusDetails.setText("Target: " + server.getName() + " | Domain: " + domain.getName() + " | Project: " + project.getName() + " | Test ID: " + test.getId());

        Long savedProfileId = null;

        // Check if user chose to save this configuration to the database table for future rerun
        if (Boolean.TRUE.equals(saveProfileCheckbox.getValue())) {
            String profileName = profileNameField.getValue();
            if (profileName == null || profileName.isBlank()) {
                profileName = "Profile_" + test.getName() + "_" + System.currentTimeMillis();
            }

            LreSavedRunConfigDTO profileDTO = new LreSavedRunConfigDTO();
            profileDTO.setProfileName(profileName.trim());
            profileDTO.setServerConfigId(server.getId());
            profileDTO.setDomainName(domain.getName());
            profileDTO.setProjectName(project.getName());
            profileDTO.setTestId(test.getId());
            profileDTO.setTestName(test.getName());
            profileDTO.setVusers(vusersField.getValue() != null ? vusersField.getValue() : 100);
            profileDTO.setDurationInMinutes(durationField.getValue() != null ? durationField.getValue() : 30);
            profileDTO.setTimeslotId(timeslotIdField.getValue() != null ? timeslotIdField.getValue().longValue() : null);
            profileDTO.setPostRunAction(postRunActionSelect.getValue());
            profileDTO.setDescription("Saved on-demand profile for automated execution");

            try {
                LreSavedRunConfigDTO saved = lreService.saveProfile(profileDTO);
                savedProfileId = saved.getId();
                Notification.show("Configuration saved to table as profile: '" + saved.getProfileName() + "'", 3500, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Failed to save profile: " + ex.getMessage(), 3500, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }

        LreRunRequestDTO request = new LreRunRequestDTO(
                test.getId(),
                null,
                timeslotIdField.getValue() != null ? timeslotIdField.getValue().longValue() : null,
                vusersField.getValue() != null ? vusersField.getValue() : 100,
                durationField.getValue() != null ? durationField.getValue() : 30,
                postRunActionSelect.getValue()
        );

        UI currentUi = UI.getCurrent();

        // Trigger Async Execution
        lreService.executeTestAsync(
                server.getId(),
                domain.getName(),
                project.getName(),
                request,
                test.getName(),
                savedProfileId
        ).thenAccept(history -> {
            if (currentUi != null) {
                currentUi.access(() -> {
                    executeButton.setEnabled(true);
                    executionProgressBar.setVisible(false);
                    statusLabel.setText("Execution Dispatched! Status: " + history.getStatus() + (history.getRunId() != null ? " (Run ID: " + history.getRunId() + ")" : ""));
                    statusDetails.setText(history.getExecutionMessage());
                    refreshHistory();
                    Notification.show("Test Run Dispatched Successfully", 3000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                });
            }
        }).exceptionally(ex -> {
            if (currentUi != null) {
                currentUi.access(() -> {
                    executeButton.setEnabled(true);
                    executionProgressBar.setVisible(false);
                    statusLabel.setText("Execution Failed");
                    statusDetails.setText("Error: " + ex.getMessage());
                    refreshHistory();
                    Notification.show("Execution failed: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                });
            }
            return null;
        });
    }

    private VerticalLayout createRecentExecutionsSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);

        HorizontalLayout top = new HorizontalLayout();
        top.setWidthFull();
        top.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        H3 sectionTitle = new H3("Recent On-Demand Executions");
        sectionTitle.addClassNames(LumoUtility.Margin.NONE);

        Button refreshBtn = new Button("Refresh History", VaadinIcon.REFRESH.create(), e -> refreshHistory());
        refreshBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        top.add(sectionTitle, refreshBtn);
        top.expand(sectionTitle);

        historyGrid.setSizeFull();
        historyGrid.addColumn(LreExecutionHistoryDTO::getId).setHeader("Log ID").setAutoWidth(true).setFlexGrow(0);
        historyGrid.addColumn(h -> h.getRunId() != null ? String.valueOf(h.getRunId()) : "Pending").setHeader("LRE Run ID").setAutoWidth(true);
        historyGrid.addColumn(LreExecutionHistoryDTO::getServerConfigName).setHeader("LRE Host").setAutoWidth(true);
        historyGrid.addColumn(LreExecutionHistoryDTO::getDomainName).setHeader("Domain").setAutoWidth(true);
        historyGrid.addColumn(LreExecutionHistoryDTO::getProjectName).setHeader("Project").setAutoWidth(true);
        historyGrid.addColumn(LreExecutionHistoryDTO::getTestName).setHeader("Test Name").setAutoWidth(true).setFlexGrow(1);
        historyGrid.addColumn(LreExecutionHistoryDTO::getVusers).setHeader("Vusers").setAutoWidth(true);
        historyGrid.addColumn(LreExecutionHistoryDTO::getDurationInMinutes).setHeader("Duration (min)").setAutoWidth(true);

        historyGrid.addComponentColumn(this::createStatusBadge).setHeader("Status").setAutoWidth(true);
        historyGrid.addColumn(LreExecutionHistoryDTO::getInitiatedAt).setHeader("Initiated At").setAutoWidth(true);

        historyGrid.addComponentColumn(h -> {
            Button refreshStatusBtn = new Button(VaadinIcon.REFRESH.create(), e -> {
                lreService.refreshExecutionStatus(h.getId());
                refreshHistory();
            });
            refreshStatusBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            refreshStatusBtn.setTooltipText("Query status from LRE");
            return refreshStatusBtn;
        }).setHeader("Check Status").setAutoWidth(true);

        layout.add(top, historyGrid);
        return layout;
    }

    private Span createStatusBadge(LreExecutionHistoryDTO history) {
        String status = history.getStatus() != null ? history.getStatus() : "UNKNOWN";
        Span badge = new Span(status);
        if (status.equalsIgnoreCase("RUNNING") || status.equalsIgnoreCase("INITIALIZING")) {
            badge.getElement().getThemeList().add("badge primary pill");
        } else if (status.equalsIgnoreCase("FINISHED") || status.equalsIgnoreCase("PASSED") || status.equalsIgnoreCase("COMPLETED")) {
            badge.getElement().getThemeList().add("badge success pill");
        } else if (status.equalsIgnoreCase("FAILED") || status.equalsIgnoreCase("ERROR")) {
            badge.getElement().getThemeList().add("badge error pill");
        } else {
            badge.getElement().getThemeList().add("badge contrast pill");
        }
        return badge;
    }

    private void refreshHistory() {
        List<LreExecutionHistoryDTO> list = lreService.getRecentExecutions();
        historyGrid.setItems(list);
    }
}