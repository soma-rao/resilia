package com.resilia.ui;

import com.resilia.dto.DomainDTO;
import com.resilia.dto.ProjectDTO;
import com.resilia.service.PerformanceCenterService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Collections;
import java.util.List;

@PageTitle("Domains & Projects | Resilia Performance Center")
@Route(value = "domains", layout = MainLayout.class)
public class DomainsView extends VerticalLayout {

    private final PerformanceCenterService service;
    private final Grid<DomainDTO> domainGrid = new Grid<>(DomainDTO.class, false);
    private final Grid<ProjectDTO> projectGrid = new Grid<>(ProjectDTO.class, false);
    private DomainDTO selectedDomain;
    private final H3 projectsHeading = new H3("Projects");
    private final Button addProjectButton = new Button("New Project", VaadinIcon.PLUS.create());

    public DomainsView(PerformanceCenterService service) {
        this.service = service;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader(), createMainContent());
        refreshDomains();
    }

    private VerticalLayout createHeader() {
        VerticalLayout headerLayout = new VerticalLayout();
        headerLayout.setPadding(false);
        headerLayout.setSpacing(false);

        H2 title = new H2("Domains & Projects Management");
        Span subtitle = new Span("Organize performance testing workspaces and projects");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        headerLayout.add(title, subtitle);
        return headerLayout;
    }

    private SplitLayout createMainContent() {
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(40);

        // Domains Panel
        VerticalLayout domainsPanel = new VerticalLayout();
        domainsPanel.setSizeFull();
        domainsPanel.setPadding(false);

        Button addDomainButton = new Button("New Domain", VaadinIcon.PLUS.create(), e -> openAddDomainDialog());
        addDomainButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout domainHeader = new HorizontalLayout(new H3("Domains"), addDomainButton);
        domainHeader.setWidthFull();
        domainHeader.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        domainHeader.expand(domainHeader.getComponentAt(0));

        domainGrid.addColumn(DomainDTO::getName).setHeader("Domain Name").setAutoWidth(true).setFlexGrow(1);
        domainGrid.addColumn(DomainDTO::getDescription).setHeader("Description").setAutoWidth(true).setFlexGrow(2);
        domainGrid.asSingleSelect().addValueChangeListener(event -> {
            selectedDomain = event.getValue();
            refreshProjects();
        });

        domainsPanel.add(domainHeader, domainGrid);

        // Projects Panel
        VerticalLayout projectsPanel = new VerticalLayout();
        projectsPanel.setSizeFull();
        projectsPanel.setPadding(false);

        addProjectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addProjectButton.setEnabled(false);
        addProjectButton.addClickListener(e -> openAddProjectDialog());

        HorizontalLayout projectHeader = new HorizontalLayout(projectsHeading, addProjectButton);
        projectHeader.setWidthFull();
        projectHeader.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        projectHeader.expand(projectsHeading);

        projectGrid.addColumn(ProjectDTO::getName).setHeader("Project Name").setAutoWidth(true).setFlexGrow(1);
        projectGrid.addColumn(ProjectDTO::getDescription).setHeader("Description").setAutoWidth(true).setFlexGrow(2);
        projectGrid.addComponentColumn(p -> {
            Span badge = new Span(p.isActive() ? "Active" : "Inactive");
            badge.getElement().getThemeList().add(p.isActive() ? "badge success" : "badge contrast");
            return badge;
        }).setHeader("Status").setAutoWidth(true);

        projectsPanel.add(projectHeader, projectGrid);

        splitLayout.addToPrimary(domainsPanel);
        splitLayout.addToSecondary(projectsPanel);
        return splitLayout;
    }

    private void refreshDomains() {
        List<DomainDTO> domains = service.getAllDomains();
        domainGrid.setItems(domains);
        if (!domains.isEmpty() && selectedDomain == null) {
            domainGrid.asSingleSelect().setValue(domains.get(0));
        }
    }

    private void refreshProjects() {
        if (selectedDomain != null) {
            projectsHeading.setText("Projects in '" + selectedDomain.getName() + "'");
            addProjectButton.setEnabled(true);
            projectGrid.setItems(service.getProjectsByDomain(selectedDomain.getName()));
        } else {
            projectsHeading.setText("Select a Domain to view Projects");
            addProjectButton.setEnabled(false);
            projectGrid.setItems(Collections.emptyList());
        }
    }

    private void openAddDomainDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Create New Domain");

        TextField nameField = new TextField("Domain Name");
        nameField.setRequired(true);
        TextField descField = new TextField("Description");

        FormLayout formLayout = new FormLayout(nameField, descField);
        dialog.add(formLayout);

        Button saveButton = new Button("Create", e -> {
            if (nameField.isEmpty()) {
                Notification.show("Domain name is required", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            DomainDTO dto = new DomainDTO();
            dto.setName(nameField.getValue().trim());
            dto.setDescription(descField.getValue());
            try {
                service.createDomain(dto);
                Notification.show("Domain created successfully", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                refreshDomains();
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Failed to create domain: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void openAddProjectDialog() {
        if (selectedDomain == null) return;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Create Project in '" + selectedDomain.getName() + "'");

        TextField nameField = new TextField("Project Name");
        nameField.setRequired(true);
        TextField descField = new TextField("Description");
        Checkbox activeCheck = new Checkbox("Active", true);

        FormLayout formLayout = new FormLayout(nameField, descField, activeCheck);
        dialog.add(formLayout);

        Button saveButton = new Button("Create", e -> {
            if (nameField.isEmpty()) {
                Notification.show("Project name is required", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            ProjectDTO dto = new ProjectDTO();
            dto.setName(nameField.getValue().trim());
            dto.setDescription(descField.getValue());
            dto.setActive(activeCheck.getValue());
            try {
                service.createProject(selectedDomain.getName(), dto);
                Notification.show("Project created successfully", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                refreshProjects();
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Failed to create project: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }
}
