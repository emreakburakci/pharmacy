package com.example.application.views.list;


import com.example.application.data.entity.Customer;
import com.example.application.data.service.CustomerService;
import com.example.application.util.ResourceBundleUtil;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.security.PermitAll;


@Component
@Scope("prototype")
@Route(value = "", layout = MainLayout.class)
@PageTitle("Emre HBYS")

@PermitAll
public class CustomerListView extends VerticalLayout {

    private Grid<Customer> grid = new Grid<>(Customer.class);
    private TextField filterText = new TextField();
    private CustomerForm form;
    private CustomerService customerService;

    private Customer selectedCustomer;
    private ResourceBundleUtil rb;
    private String currentPrincipalName;

    private String lang;

    public CustomerListView(CustomerService customerService) {
        this.customerService = customerService;
        lang = VaadinSession.getCurrent().getAttribute("language").toString();
        rb = new ResourceBundleUtil(lang);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UI.getCurrent().getPage().setTitle(rb.getString("personnelListTitle"));

        currentPrincipalName = authentication.getName();
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new CustomerForm(lang);
        form.setWidth("25em");
        form.addListener(CustomerForm.SaveEvent.class, this::saveCustomer);
        form.addListener(CustomerForm.DeleteEvent.class, this::deleteCustomer);
        form.addListener(CustomerForm.CloseEvent.class, e -> closeEditor());
    }

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns("name", "lastName");

        grid.addColumn(customer -> CustomerService.formatPhoneNumber(customer.getPhone())).setAutoWidth(true).setKey("phone");


        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> {

            editCustomer(event.getValue());
        });

        grid.getColumnByKey("name").setHeader(rb.getString("name"));
        grid.getColumnByKey("lastName").setHeader(rb.getString("lastName"));
        grid.getColumnByKey("phone").setHeader(rb.getString("phone"));
    }


    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder(rb.getString("filterTextPlaceHolder"));
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addCustomerButton = new Button(rb.getString("addCustomer"));
        addCustomerButton.addClickListener(click -> addCustomer());


        HorizontalLayout toolbar = new HorizontalLayout(filterText, addCustomerButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void saveCustomer(CustomerForm.SaveEvent event) {


        customerService.saveCustomer(event.getCustomer());

        updateList();
        closeEditor();
    }

    private void hasRelationNotification(Customer p) {

        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(3000);
        Div text = new Div(new Text(p.getName() + " " + rb.getString("hasRelationError")));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }

    private void deleteCustomer(CustomerForm.DeleteEvent event) {

        customerService.deleteCustomer(event.getCustomer());
        updateList();
        closeEditor();
    }

    public void editCustomer(Customer customer) {

        selectedCustomer = customer;

        if (customer == null) {
            closeEditor();
        }
            form.setCustomer(customer);
            form.setVisible(true);
            addClassName("editing");

        }


    private void addCustomer() {

        grid.asSingleSelect().clear();
        editCustomer(new Customer());

    }

    private void closeEditor() {
        form.setCustomer(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(customerService.findAllCustomer());
    }
}
