package com.resilia.ui;

import com.resilia.dto.DomainDTO;
import com.resilia.dto.ProjectDTO;
import com.resilia.dto.TimeslotDTO;
import com.resilia.service.PerformanceCenterService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@PageTitle("Timeslots | Resilia Performance Center")
@Route(value = "timeslots", layout = MainLayout.class)
public class TimeslotsView extends VerticalLayout {

    private final PerformanceCenterService service;

    private final ComboBox<DomainDTO> domainSelect = new ComboBox<>("Domain");
    private final ComboBox<ProjectDTO> projectSelect = new ComboBox<>("Project");

    private final Grid<TimeslotDTO> timeslotGrid = new Grid<>(TimeslotDTO.class, false);
    private final Button reserveBtn = new Button("Reserve Timeslot", VaadinIcon.CALENDAR.create());

    public TimeslotsView(PerformanceCenterService service) {
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

        H2 title = new H2("Timeslot Resource Reservations");
        Span subtitle = new Span("Schedule exclusive testing windows and allocate load capacity");
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
                    refreshTimeslots();
                }
            } else {
                projectSelect.clear();
                projectSelect.setItems(Collections.emptyList());
                refreshTimeslots();
            }
        });

        projectSelect.addValueChangeListener(e -> refreshTimeslots());

        reserveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        reserveBtn.setEnabled(false);
        reserveBtn.addClickListener(e -> openReserveDialog());

        HorizontalLayout toolbar = new HorizontalLayout(domainSelect, projectSelect, reserveBtn);
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

    private Grid<TimeslotDTO> createGrid() {
        timeslotGrid.setSizeFull();

        timeslotGrid.addColumn(TimeslotDTO::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        timeslotGrid.addColumn(TimeslotDTO::getName).setHeader("Timeslot Name").setAutoWidth(true).setFlexGrow(1);
        timeslotGrid.addColumn(TimeslotDTO::getStartTime).setHeader("Start Time").setAutoWidth(true);
        timeslotGrid.addColumn(TimeslotDTO::getEndTime).setHeader("End Time").setAutoWidth(true);
        timeslotGrid.addColumn(TimeslotDTO::getVusersAllocated).setHeader("Vusers Allocated").setAutoWidth(true);
        timeslotGrid.addComponentColumn(this::createStatusBadge).setHeader("Status").setAutoWidth(true);

        return timeslotGrid;
    }

    private Span createStatusBadge(TimeslotDTO ts) {
        String status = ts.getStatus() != null ? ts.getStatus() : "RESERVED";
        Span badge = new Span(status);
        if ("RESERVED".equalsIgnoreCase(status) || "ACTIVE".equalsIgnoreCase(status)) {
            badge.getElement().getThemeList().add("badge success pill");
        } else if ("COMPLETED".equalsIgnoreCase(status) || "CLOSED".equalsIgnoreCase(status)) {
            badge.getElement().getThemeList().add("badge contrast pill");
        } else {
            badge.getElement().getThemeList().add("badge error pill");
        }
        return badge;
    }

    private void refreshTimeslots() {
        DomainDTO domain = domainSelect.getValue();
        ProjectDTO project = projectSelect.getValue();

        if (domain != null && project != null) {
            reserveBtn.setEnabled(true);
            timeslotGrid.setItems(service.getTimeslots(domain.getName(), project.getName()));
        } else {
            reserveBtn.setEnabled(false);
            timeslotGrid.setItems(Collections.emptyList());
        }
    }

    private void openReserveDialog() {
        if (domainSelect.getValue() == null || projectSelect.getValue() == null) return;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Reserve Timeslot");

        TextField nameField = new TextField("Timeslot Name");
        nameField.setRequired(true);

        DateTimePicker startField = new DateTimePicker("Start Time");
        startField.setValue(LocalDateTime.now());

        DateTimePicker endField = new DateTimePicker("End Time");
        endField.setValue(LocalDateTime.now().plusHours(4));

        IntegerField vusersField = new IntegerField("Allocated Vusers Capacity");
        vusersField.setValue(2000);
        vusersField.setMin(1);

        FormLayout formLayout = new FormLayout(nameField, vusersField, startField, endField);
        dialog.add(formLayout);

        Button saveButton = new Button("Reserve", e -> {
            if (nameField.isEmpty() || startField.getValue() == null || endField.getValue() == null) {
                Notification.show("Name, start time, and end time are required", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            if (endField.getValue().isBefore(startField.getValue())) {
                Notification.show("End time must be after start time", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            TimeslotDTO dto = new TimeslotDTO();
            dto.setName(nameField.getValue().trim());
            dto.setStartTime(startField.getValue());
            dto.setEndTime(endField.getValue());
            dto.setVusersAllocated(vusersField.getValue() != null ? vusersField.getValue() : 1000);
            dto.setStatus("RESERVED");

            try {
                service.reserveTimeslot(domainSelect.getValue().getName(), projectSelect.getValue().getName(), dto);
                Notification.show("Timeslot reserved successfully", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                refreshTimeslots();
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Failed to reserve timeslot: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }
}
