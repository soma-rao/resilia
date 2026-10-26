package com.resilia.lre.ui;

import com.resilia.lre.dto.LreServerConfigDTO;
import com.resilia.lre.service.LreService;
import com.resilia.ui.MainLayout;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

@PageTitle("LRE Host Configurations | Resilia Performance Center")
@Route(value = "lre/servers", layout = MainLayout.class)
public class LreServerConfigsView extends VerticalLayout {

    private final LreService lreService;
    private final Grid<LreServerConfigDTO> grid = new Grid<>(LreServerConfigDTO.class, false);

    public LreServerConfigsView(LreService lreService) {
        this.lreService = lreService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createHeader(), createToolbar(), grid);
        configureGrid();
        refreshGrid();
    }

    private VerticalLayout createHeader() {
        VerticalLayout headerLayout = new VerticalLayout();
        headerLayout.setPadding(false);
        headerLayout.setSpacing(false);

        H2 title = new H2("LoadRunner Enterprise (LRE) Server Configurations");
        Span subtitle = new Span("Configure multiple LRE Host instances, authentication credentials, and connection parameters");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        headerLayout.add(title, subtitle);
        return headerLayout;
    }

    private HorizontalLayout createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        H3 sectionTitle = new H3("Configured LRE Hosts");
        sectionTitle.addClassNames(LumoUtility.Margin.NONE);

        Button addServerBtn = new Button("New LRE Host Config", VaadinIcon.PLUS.create(), e -> openEditDialog(new LreServerConfigDTO()));
        addServerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        toolbar.add(sectionTitle, addServerBtn);
        toolbar.expand(sectionTitle);
        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(LreServerConfigDTO::getName).setHeader("Configuration Name").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(LreServerConfigDTO::getBaseUrl).setHeader("Base URL").setAutoWidth(true).setFlexGrow(2);
        grid.addColumn(LreServerConfigDTO::getUsername).setHeader("User ID").setAutoWidth(true);
        grid.addColumn(s -> s.getClientId() != null && !s.getClientId().isBlank() ? s.getClientId() : "-").setHeader("Client ID").setAutoWidth(true);

        grid.addComponentColumn(s -> {
            Span badge = new Span(s.isActive() ? "Active" : "Inactive");
            badge.getElement().getThemeList().add(s.isActive() ? "badge success" : "badge error");
            return badge;
        }).setHeader("Status").setAutoWidth(true);

        grid.addComponentColumn(this::createActionButtons).setHeader("Actions").setAutoWidth(true).setFlexGrow(1);
    }

    private HorizontalLayout createActionButtons(LreServerConfigDTO config) {
        Button testBtn = new Button("Test Auth", VaadinIcon.CONNECT.create(), e -> testConnection(config));
        testBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        Button editBtn = new Button("Edit", VaadinIcon.EDIT.create(), e -> openEditDialog(config));
        editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        Button deleteBtn = new Button(VaadinIcon.TRASH.create(), e -> deleteConfig(config));
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(testBtn, editBtn, deleteBtn);
        actions.setSpacing(true);
        return actions;
    }

    private void testConnection(LreServerConfigDTO config) {
        try {
            boolean success = lreService.testConnection(config.getId());
            if (success) {
                Notification.show("Authentication successful with LRE server: " + config.getName(), 3500, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification.show("Authentication failed with LRE server: " + config.getName(), 3500, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (Exception ex) {
            Notification.show("Connection test error: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteConfig(LreServerConfigDTO config) {
        lreService.deleteServerConfig(config.getId());
        refreshGrid();
        Notification.show("LRE Configuration removed", 2500, Notification.Position.BOTTOM_START);
    }

    private void openEditDialog(LreServerConfigDTO config) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(config.getId() == null ? "Add LRE Host Configuration" : "Edit LRE Host Configuration");
        dialog.setWidth("650px");

        FormLayout form = new FormLayout();

        TextField nameField = new TextField("Configuration Name");
        nameField.setRequired(true);
        nameField.setValue(config.getName() != null ? config.getName() : "");

        TextField hostField = new TextField("LRE Host / Domain");
        hostField.setPlaceholder("e.g. lre-server.corp.local or localhost");
        hostField.setRequired(true);
        hostField.setValue(config.getHost() != null ? config.getHost() : "");

        IntegerField portField = new IntegerField("Port");
        portField.setValue(config.getPort() > 0 ? config.getPort() : 443);

        Checkbox useHttps = new Checkbox("Use HTTPS (SSL/TLS)", config.isUseHttps());

        TextField userIdField = new TextField("User ID");
        userIdField.setPlaceholder("LRE Username / ALM User");
        userIdField.setValue(config.getUsername() != null ? config.getUsername() : "");

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setValue(config.getPassword() != null ? config.getPassword() : "");

        TextField clientIdField = new TextField("Client ID (OAuth - Optional)");
        clientIdField.setValue(config.getClientId() != null ? config.getClientId() : "");

        PasswordField clientSecretField = new PasswordField("Client Secret (OAuth - Optional)");
        clientSecretField.setValue(config.getClientSecret() != null ? config.getClientSecret() : "");

        TextField descriptionField = new TextField("Description");
        descriptionField.setValue(config.getDescription() != null ? config.getDescription() : "");

        Checkbox activeField = new Checkbox("Active", config.isActive());

        form.add(nameField, descriptionField, hostField, portField, useHttps, activeField, userIdField, passwordField, clientIdField, clientSecretField);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        form.setColspan(nameField, 2);
        form.setColspan(descriptionField, 2);
        form.setColspan(hostField, 2);

        Button saveBtn = new Button("Save Configuration", e -> {
            if (nameField.isEmpty() || hostField.isEmpty()) {
                Notification.show("Please provide configuration name and host", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            config.setName(nameField.getValue().trim());
            config.setHost(hostField.getValue().trim());
            config.setPort(portField.getValue() != null ? portField.getValue() : (useHttps.getValue() ? 443 : 80));
            config.setUseHttps(Boolean.TRUE.equals(useHttps.getValue()));
            config.setUsername(userIdField.getValue().trim());
            config.setPassword(passwordField.getValue());
            config.setClientId(clientIdField.getValue().trim());
            config.setClientSecret(clientSecretField.getValue());
            config.setDescription(descriptionField.getValue().trim());
            config.setActive(Boolean.TRUE.equals(activeField.getValue()));

            try {
                lreService.saveServerConfig(config);
                dialog.close();
                refreshGrid();
                Notification.show("LRE Configuration saved successfully", 3000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Failed to save configuration: " + ex.getMessage(), 3500, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancel", e -> dialog.close());

        dialog.add(form);
        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }

    private void refreshGrid() {
        List<LreServerConfigDTO> list = lreService.getAllServerConfigs();
        grid.setItems(list);
    }
}