package com.resilia.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Resilia Performance Center");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM,
                LumoUtility.FontWeight.BOLD
        );

        Span badge = new Span("LoadRunner Enterprise");
        badge.getElement().getThemeList().add("badge success pill");
        badge.addClassNames(LumoUtility.Margin.Right.MEDIUM);

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, badge);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.BoxShadow.SMALL
        );

        addToNavbar(header);
    }

    private void createDrawer() {
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create()));
        nav.addItem(new SideNavItem("Domains & Projects", DomainsView.class, VaadinIcon.BUILDING.create()));
        nav.addItem(new SideNavItem("Tests & Scenarios", TestsView.class, VaadinIcon.FLASK.create()));
        nav.addItem(new SideNavItem("Test Runs", RunsView.class, VaadinIcon.PLAY_CIRCLE.create()));
        nav.addItem(new SideNavItem("Load Generators", LoadGeneratorsView.class, VaadinIcon.SERVER.create()));
        nav.addItem(new SideNavItem("Timeslots", TimeslotsView.class, VaadinIcon.CALENDAR.create()));

        SideNavItem lreSection = new SideNavItem("LRE On-Demand");
        lreSection.addItem(new SideNavItem("Run On Demand", com.resilia.lre.ui.LreOnDemandRunView.class, VaadinIcon.ROCKET.create()));
        lreSection.addItem(new SideNavItem("Saved Profiles", com.resilia.lre.ui.LreSavedProfilesView.class, VaadinIcon.AUTOMATION.create()));
        lreSection.addItem(new SideNavItem("LRE Hosts Config", com.resilia.lre.ui.LreServerConfigsView.class, VaadinIcon.COG.create()));
        nav.addItem(lreSection);

        addToDrawer(nav);
    }
}
