package com.resilia.ui;

import com.resilia.dto.DomainDTO;
import com.resilia.dto.LoadGeneratorDTO;
import com.resilia.dto.ProjectDTO;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Collections;
import java.util.List;

@PageTitle("Load Generators | Resilia Performance Center")
@Route(value = "load-generators", layout = MainLayout.class)
public class LoadGeneratorsView extends VerticalLayout {

    private final PerformanceCenterService service;

    private final ComboBox<DomainDTO> domainSelect = new ComboBox<>("Domain");
    private final ComboBox<ProjectDTO> projectSelect = new ComboBox<>("Project");

    private final Grid<LoadGeneratorDTO> lgGrid = new Grid<>(LoadGeneratorDTO.class, false);
    private final Button addLgBtn = new Button("Register Load Generator", VaadinIcon.PLUS.create());

    public LoadGeneratorsView(PerformanceCenterService service) {
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

        H2 title = new H2("Load Generators Management");
        Span subtitle = new Span("Register and monitor distributed load injection agent machines");
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
                    refreshGenerators();
                }
            } else {
                projectSelect.clear();
                projectSelect.setItems(Collections.emptyList());
                refreshGenerators();
            }
        });

        projectSelect.addValueChangeListener(e -> refreshGenerators());

        addLgBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addLgBtn.setEnabled(false);
        addLgBtn.addClickListener(e -> openAddDialog());

        HorizontalLayout toolbar = new HorizontalLayout(domainSelect, projectSelect, addLgBtn);
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

    private Grid<LoadGeneratorDTO> createGrid() {
        lgGrid.setSizeFull();

        lgGrid.addColumn(LoadGeneratorDTO::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        lgGrid.addColumn(LoadGeneratorDTO::getName).setHeader("Host Name / Alias").setAutoWidth(true).setFlexGrow(1);
        lgGrid.addColumn(LoadGeneratorDTO::getHost).setHeader("IP / FQDN").setAutoWidth(true);
        lgGrid.addColumn(LoadGeneratorDTO::getPort).setHeader("Port").setAutoWidth(true);
        lgGrid.addColumn(LoadGeneratorDTO::getCapacity).setHeader("Vusers Capacity").setAutoWidth(true);
        lgGrid.addColumn(LoadGeneratorDTO::getLocation).setHeader("Location / Region").setAutoWidth(true);
        lgGrid.addComponentColumn(this::createStatusBadge).setHeader("Status").setAutoWidth(true);

        return lgGrid;
    }

    private Span createStatusBadge(LoadGeneratorDTO lg) {
        String status = lg.getStatus() != null ? lg.getStatus() : "Operational";
        Span badge = new Span(status);
        if ("Operational".equalsIgnoreCase(status) || "Ready".equalsIgnoreCase(status) || "Active".equalsIgnoreCase(status)) {
            badge.getElement().getThemeList().add("badge success pill");
        } else if ("Busy".equalsIgnoreCase(status) || "Testing".equalsIgnoreCase(status)) {
            badge.getElement().getThemeList().add("badge primary pill");
        } else {
            badge.getElement().getThemeList().add("badge error pill");
        }
        return badge;
    }

    private void refreshGenerators() {
        DomainDTO domain = domainSelect.getValue();
        ProjectDTO project = projectSelect.getValue();

        if (domain != null && project != null) {
            addLgBtn.setEnabled(true);
            lgGrid.setItems(service.getLoadGenerators(domain.getName(), project.getName()));
        } else {
            addLgBtn.setEnabled(false);
            lgGrid.setItems(Collections.emptyList());
        }
    }

    private void openAddDialog() {
        if (domainSelect.getValue() == null || projectSelect.getValue() == null) return;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Register Load Generator");

        TextField nameField = new TextField("Generator Name / Alias");
        nameField.setRequired(true);

        TextField hostField = new TextField("Host IP or FQDN");
        hostField.setRequired(true);

        IntegerField portField = new IntegerField("Port");
        portField.setValue(443);

        IntegerField capacityField = new IntegerField("Capacity (Vusers)");
        capacityField.setValue(5000);

        TextField locationField = new TextField("Location / Region");
        locationField.setValue("US East (N. Virginia)");

        ComboBox<String> statusField = new ComboBox<>("Status");
        statusField.setItems("Operational", "Maintenance", "Standby");
        statusField.setValue("Operational");

        FormLayout formLayout = new FormLayout(nameField, hostField, portField, capacityField, locationField, statusField);
        dialog.add(formLayout);

        Button saveButton = new Button("Register", e -> {
            if (nameField.isEmpty() || hostField.isEmpty()) {
                Notification.show("Name and Host are required", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            LoadGeneratorDTO dto = new LoadGeneratorDTO();
            dto.setName(nameField.getValue().trim());
            dto.setHost(hostField.getValue().trim());
            dto.setPort(portField.getValue() != null ? portField.getValue() : 443);
            dto.setCapacity(capacityField.getValue() != null ? capacityField.getValue() : 1000);
            dto.setLocation(locationField.getValue());
            dto.setStatus(statusField.getValue());

            try {
                service.registerLoadGenerator(domainSelect.getValue().getName(), projectSelect.getValue().getName(), dto);
                Notification.show("Load Generator registered successfully", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                refreshGenerators();
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Failed to register load generator: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }
}
