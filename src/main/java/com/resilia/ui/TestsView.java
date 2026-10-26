package com.resilia.ui;

import com.resilia.dto.*;
import com.resilia.service.PerformanceCenterService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Collections;
import java.util.List;

@PageTitle("Tests & Scenarios | Resilia Performance Center")
@Route(value = "tests", layout = MainLayout.class)
public class TestsView extends VerticalLayout {

    private final PerformanceCenterService service;

    private final ComboBox<DomainDTO> domainSelect = new ComboBox<>("Domain");
    private final ComboBox<ProjectDTO> projectSelect = new ComboBox<>("Project");

    private final Grid<TestDTO> testGrid = new Grid<>(TestDTO.class, false);
    private final Grid<TestSetDTO> testSetGrid = new Grid<>(TestSetDTO.class, false);

    private final Button addTestBtn = new Button("New Test", VaadinIcon.PLUS.create());
    private final Button addTestSetBtn = new Button("New Test Set", VaadinIcon.PLUS.create());

    private final VerticalLayout testsTabContent = new VerticalLayout();
    private final VerticalLayout testSetsTabContent = new VerticalLayout();

    public TestsView(PerformanceCenterService service) {
        this.service = service;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader(), createDomainProjectSelectors(), createTabsAndContent());
        initSelectors();
    }

    private VerticalLayout createHeader() {
        VerticalLayout headerLayout = new VerticalLayout();
        headerLayout.setPadding(false);
        headerLayout.setSpacing(false);

        H2 title = new H2("Performance Tests & Test Sets");
        Span subtitle = new Span("Design, configure, and manage LoadRunner performance test assets");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        headerLayout.add(title, subtitle);
        return headerLayout;
    }

    private HorizontalLayout createDomainProjectSelectors() {
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
                    refreshData();
                }
            } else {
                projectSelect.clear();
                projectSelect.setItems(Collections.emptyList());
                refreshData();
            }
        });

        projectSelect.addValueChangeListener(e -> refreshData());

        HorizontalLayout selectors = new HorizontalLayout(domainSelect, projectSelect);
        selectors.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        return selectors;
    }

    private void initSelectors() {
        List<DomainDTO> domains = service.getAllDomains();
        domainSelect.setItems(domains);
        if (!domains.isEmpty()) {
            domainSelect.setValue(domains.get(0));
        }
    }

    private VerticalLayout createTabsAndContent() {
        Tab tabTests = new Tab("Tests / Scenarios");
        Tab tabTestSets = new Tab("Test Sets");
        Tabs tabs = new Tabs(tabTests, tabTestSets);

        setupTestsTab();
        setupTestSetsTab();

        testSetsTabContent.setVisible(false);

        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            if (selectedTab.equals(tabTests)) {
                testsTabContent.setVisible(true);
                testSetsTabContent.setVisible(false);
            } else {
                testsTabContent.setVisible(false);
                testSetsTabContent.setVisible(true);
            }
        });

        VerticalLayout container = new VerticalLayout(tabs, testsTabContent, testSetsTabContent);
        container.setSizeFull();
        container.setPadding(false);
        return container;
    }

    private void setupTestsTab() {
        testsTabContent.setSizeFull();
        testsTabContent.setPadding(false);

        addTestBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addTestBtn.addClickListener(e -> openAddTestDialog());

        HorizontalLayout toolbar = new HorizontalLayout(addTestBtn);
        toolbar.setWidthFull();

        testGrid.addColumn(TestDTO::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        testGrid.addColumn(TestDTO::getName).setHeader("Test Name").setAutoWidth(true).setFlexGrow(1);
        testGrid.addColumn(TestDTO::getType).setHeader("Type").setAutoWidth(true);
        testGrid.addColumn(TestDTO::getVusers).setHeader("Vusers").setAutoWidth(true);
        testGrid.addColumn(TestDTO::getDurationInMinutes).setHeader("Duration (min)").setAutoWidth(true);
        testGrid.addColumn(TestDTO::getFolderPath).setHeader("Folder Path").setAutoWidth(true);
        testGrid.addColumn(TestDTO::getDescription).setHeader("Description").setAutoWidth(true);

        testGrid.addComponentColumn(test -> {
            Button runBtn = new Button("Run", VaadinIcon.PLAY.create(), e -> openRunTestDialog(test));
            runBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);

            Button deleteBtn = new Button(VaadinIcon.TRASH.create(), e -> {
                if (getSelectedDomain() != null && getSelectedProject() != null) {
                    service.deleteTest(getSelectedDomain().getName(), getSelectedProject().getName(), test.getId());
                    Notification.show("Test deleted", 3000, Notification.Position.TOP_CENTER);
                    refreshData();
                }
            });
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);

            HorizontalLayout actions = new HorizontalLayout(runBtn, deleteBtn);
            return actions;
        }).setHeader("Actions").setAutoWidth(true);

        testsTabContent.add(toolbar, testGrid);
    }

    private void setupTestSetsTab() {
        testSetsTabContent.setSizeFull();
        testSetsTabContent.setPadding(false);

        addTestSetBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addTestSetBtn.addClickListener(e -> openAddTestSetDialog());

        HorizontalLayout toolbar = new HorizontalLayout(addTestSetBtn);
        toolbar.setWidthFull();

        testSetGrid.addColumn(TestSetDTO::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        testSetGrid.addColumn(TestSetDTO::getName).setHeader("Test Set Name").setAutoWidth(true).setFlexGrow(1);
        testSetGrid.addColumn(TestSetDTO::getFolderPath).setHeader("Folder Path").setAutoWidth(true);
        testSetGrid.addColumn(TestSetDTO::getDescription).setHeader("Description").setAutoWidth(true);
        testSetGrid.addColumn(ts -> ts.getTestIds() != null ? ts.getTestIds().size() : 0).setHeader("Tests Count").setAutoWidth(true);

        testSetsTabContent.add(toolbar, testSetGrid);
    }

    private void refreshData() {
        DomainDTO domain = domainSelect.getValue();
        ProjectDTO project = projectSelect.getValue();

        if (domain != null && project != null) {
            addTestBtn.setEnabled(true);
            addTestSetBtn.setEnabled(true);
            testGrid.setItems(service.getTests(domain.getName(), project.getName()));
            testSetGrid.setItems(service.getTestSets(domain.getName(), project.getName()));
        } else {
            addTestBtn.setEnabled(false);
            addTestSetBtn.setEnabled(false);
            testGrid.setItems(Collections.emptyList());
            testSetGrid.setItems(Collections.emptyList());
        }
    }

    private DomainDTO getSelectedDomain() {
        return domainSelect.getValue();
    }

    private ProjectDTO getSelectedProject() {
        return projectSelect.getValue();
    }

    private void openAddTestDialog() {
        if (getSelectedDomain() == null || getSelectedProject() == null) return;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Create Performance Test");

        TextField nameField = new TextField("Test Name");
        nameField.setRequired(true);

        ComboBox<String> typeField = new ComboBox<>("Test Type");
        typeField.setItems("Web-HTTP/HTML", "Web-Services-REST", "TruClient - Web", "Java Over HTTP", "DevWeb", "SAP GUI");
        typeField.setValue("Web-HTTP/HTML");

        TextField folderField = new TextField("Folder Path");
        folderField.setValue("Subject/Regression");

        IntegerField vusersField = new IntegerField("Virtual Users (Vusers)");
        vusersField.setValue(100);
        vusersField.setMin(1);

        IntegerField durationField = new IntegerField("Duration (Minutes)");
        durationField.setValue(30);
        durationField.setMin(1);

        TextField descField = new TextField("Description");

        FormLayout formLayout = new FormLayout(nameField, typeField, folderField, vusersField, durationField, descField);
        dialog.add(formLayout);

        Button saveButton = new Button("Create Test", e -> {
            if (nameField.isEmpty()) {
                Notification.show("Test name is required", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            TestCreateDTO dto = new TestCreateDTO();
            dto.setName(nameField.getValue().trim());
            dto.setType(typeField.getValue());
            dto.setFolderPath(folderField.getValue());
            dto.setVusers(vusersField.getValue() != null ? vusersField.getValue() : 10);
            dto.setDurationInMinutes(durationField.getValue() != null ? durationField.getValue() : 15);
            dto.setDescription(descField.getValue());

            try {
                service.createTest(getSelectedDomain().getName(), getSelectedProject().getName(), dto);
                Notification.show("Test created successfully", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                refreshData();
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Failed to create test: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void openAddTestSetDialog() {
        if (getSelectedDomain() == null || getSelectedProject() == null) return;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Create Test Set");

        TextField nameField = new TextField("Test Set Name");
        nameField.setRequired(true);

        TextField folderField = new TextField("Folder Path");
        folderField.setValue("Root/Releases");

        TextField descField = new TextField("Description");

        FormLayout formLayout = new FormLayout(nameField, folderField, descField);
        dialog.add(formLayout);

        Button saveButton = new Button("Create Test Set", e -> {
            if (nameField.isEmpty()) {
                Notification.show("Test Set name is required", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            TestSetDTO dto = new TestSetDTO();
            dto.setName(nameField.getValue().trim());
            dto.setFolderPath(folderField.getValue());
            dto.setDescription(descField.getValue());

            try {
                service.createTestSet(getSelectedDomain().getName(), getSelectedProject().getName(), dto);
                Notification.show("Test Set created successfully", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                refreshData();
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Failed to create test set: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void openRunTestDialog(TestDTO test) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Start Execution: " + test.getName());

        IntegerField vusersField = new IntegerField("Vusers");
        vusersField.setValue(test.getVusers() != null ? test.getVusers() : 100);

        IntegerField durationField = new IntegerField("Duration (Minutes)");
        durationField.setValue(test.getDurationInMinutes() != null ? test.getDurationInMinutes() : 30);

        ComboBox<String> postAction = new ComboBox<>("Post Run Action");
        postAction.setItems("Collate And Analyze", "Collate Results", "Do Not Collate");
        postAction.setValue("Collate And Analyze");

        List<TimeslotDTO> timeslots = service.getTimeslots(getSelectedDomain().getName(), getSelectedProject().getName());
        ComboBox<TimeslotDTO> timeslotSelect = new ComboBox<>("Reservation Timeslot (Optional)");
        timeslotSelect.setItemLabelGenerator(t -> t.getName() + " (" + t.getStatus() + ")");
        timeslotSelect.setItems(timeslots);
        if (!timeslots.isEmpty()) {
            timeslotSelect.setValue(timeslots.get(0));
        }

        FormLayout formLayout = new FormLayout(vusersField, durationField, postAction, timeslotSelect);
        dialog.add(formLayout);

        Button startButton = new Button("Launch Run", e -> {
            RunCreateRequestDTO req = new RunCreateRequestDTO();
            req.setTestId(test.getId());
            req.setVusers(vusersField.getValue());
            req.setDurationInMinutes(durationField.getValue());
            req.setPostRunAction(postAction.getValue());
            if (timeslotSelect.getValue() != null) {
                req.setTimeslotId(timeslotSelect.getValue().getId());
            }

            try {
                RunDTO startedRun = service.startRun(getSelectedDomain().getName(), getSelectedProject().getName(), req);
                Notification.show("Run #" + startedRun.getId() + " started (" + startedRun.getStatus() + ")", 4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Failed to start run: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton, startButton);
        dialog.open();
    }
}
