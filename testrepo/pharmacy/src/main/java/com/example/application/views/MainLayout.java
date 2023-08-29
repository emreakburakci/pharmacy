package com.example.application.views;

import com.example.application.security.SecurityService;
import com.example.application.util.ResourceBundleUtil;
import com.example.application.views.list.CustomerListView;
import com.example.application.views.list.SalesView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;

@Theme(themeFolder = "flowcrmtutorial")
@PWA(name = "HBYS", shortName = "HBYS", offlinePath = "offline.html", offlineResources = { "./images/offline.png" })
public class MainLayout extends AppLayout {
    private final SecurityService securityService;
    private ResourceBundleUtil rb;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;

        Object language = VaadinSession.getCurrent().getAttribute("language");

        if (language == null || ((String) language).equals("")) {

            VaadinSession.getCurrent().setAttribute("language", "English");

        }

        rb = new ResourceBundleUtil(VaadinSession.getCurrent().getAttribute("language").toString());

        createHeader();
        createDrawer();

    }

    private void createHeader() {
        H1 logo = new H1("Pharmacy Sales App");
        logo.addClassNames("text-l", "m-m");

        Button logout = new Button(rb.getString("logout"), e -> securityService.logout());

        ComboBox<String> languageCmb = new ComboBox<>( null, "Türkçe", "English");
        languageCmb.setPlaceholder(rb.getString("language"));

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, languageCmb, logout);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        languageCmb.addValueChangeListener(lang -> {

            VaadinSession.getCurrent().setAttribute("language", lang.getValue());

            refreshPage();
        });
        addToNavbar(header);

    }

    private void refreshPage() {
        UI.getCurrent().getPage().reload();
    }

    private void createDrawer() {
        RouterLink listLink = new RouterLink(rb.getString("customerList"), CustomerListView.class);
        listLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink listLink2 = new RouterLink(rb.getString("sales"), SalesView.class);
        listLink2.setHighlightCondition(HighlightConditions.sameLocation());


        
        addToDrawer(new VerticalLayout( listLink, listLink2 ));
    }
}
