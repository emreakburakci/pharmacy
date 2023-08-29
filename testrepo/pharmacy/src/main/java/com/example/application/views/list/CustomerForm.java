package com.example.application.views.list;

import com.example.application.data.entity.Customer;
import com.example.application.util.ResourceBundleUtil;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.shared.Registration;

public class CustomerForm extends FormLayout {
    private Customer customer;
    private Binder<Customer> binder;
    private TextField name, lastName, phone;
    private Button save, delete, close;
    private ResourceBundleUtil rb;

    public CustomerForm(String lang) {
        addClassName("personel-form");

        rb = new ResourceBundleUtil(lang);

        name = new TextField(rb.getString("name"));
        lastName = new TextField(rb.getString("lastName"));
        phone = new TextField(rb.getString("phone"));

        save = new Button(rb.getString("save"));
        delete = new Button(rb.getString("delete"));
        close = new Button(rb.getString("cancel"));


        binder = new BeanValidationBinder<>(Customer.class);

        binder.forField(name)
                .asRequired(rb.getString("nameRequiredMessage"))
                .bind(Customer::getName,Customer::setName);

        binder.forField(lastName)
                .asRequired(rb.getString("lastNameRequiredMessage"))
                .bind(Customer::getLastName,Customer::setLastName);





        binder.forField(phone)
                .asRequired(rb.getString("phoneRequiredMessage"))
                .withValidator(new RegexpValidator(rb.getString("phoneRegexpMessage"),"^[1-9][0-9]{9}$"))
                .bind(Customer::getPhone,Customer::setPhone);

        //binder.bindInstanceFields(this);

        binder.addStatusChangeListener(event -> {

            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            save.setEnabled(isValid && hasChanges);
        });

        name.addValueChangeListener(event -> save.setEnabled(binder.isValid()));
        lastName.addValueChangeListener(event -> save.setEnabled(binder.isValid()));
        phone.addValueChangeListener(event -> save.setEnabled(binder.isValid()));

        add(name, lastName, phone, createButtonsLayout());


    }
    private HorizontalLayout createButtonsLayout() {

      save = new Button(rb.getString("save"));
      delete = new Button(rb.getString("delete"));
      close = new Button(rb.getString("cancel"));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, customer)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));


        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        binder.readBean(customer);

    }

    private void validateAndSave() {
        try {
            binder.writeBean(customer);
            fireEvent(new SaveEvent(this, customer));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class CustomerFormEvent extends ComponentEvent<CustomerForm> {
        private Customer customer;

        protected CustomerFormEvent(CustomerForm source, Customer customer) {
            super(source, false);
            this.customer = customer;
        }

        public Customer getCustomer() {
            return customer;
        }
    }

    public static class SaveEvent extends CustomerFormEvent {
        SaveEvent(CustomerForm source, Customer customer) {
            super(source, customer);
        }
    }

    public static class DeleteEvent extends CustomerFormEvent {
        DeleteEvent(CustomerForm source, Customer customer) {
            super(source, customer);
        }

    }

    public static class CloseEvent extends CustomerFormEvent {
        CloseEvent(CustomerForm source) {
            super(source, null);
        }
    }
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}