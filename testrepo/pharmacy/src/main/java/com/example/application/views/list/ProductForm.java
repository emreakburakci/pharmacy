package com.example.application.views.list;

import com.example.application.data.entity.Product;
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
import com.vaadin.flow.shared.Registration;

public class ProductForm extends FormLayout {
    private Product product;

    private TextField name, description, unitPrice, quantityInStock;
    private Button save, delete, close;
    private Binder<Product> binder;

    private ResourceBundleUtil rb;

    public ProductForm(String lang) {

        addClassName("Patience-form");

        rb = new ResourceBundleUtil(lang);

        description = new TextField(rb.getString("TCNO"));
        unitPrice = new TextField(rb.getString("lastName"));
        name = new TextField(rb.getString("name"));
        quantityInStock = new TextField(rb.getString("phone"));

        configureBinder();

        add( name, description, unitPrice, quantityInStock, createButtonsLayout());
    }


private void configureBinder(){
    binder = new BeanValidationBinder<>(Product.class);

    binder.bindInstanceFields(this);
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
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, product)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));


        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }


    public void setProduct(Product product) {
        this.product = product;
        binder.readBean(product);

    }

    private void validateAndSave() {
        try {

            binder.writeBean(product);

            fireEvent(new SaveEvent(this, product));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }


    // Events
    public static abstract class ProductFormEvent extends ComponentEvent<ProductForm> {
        private Product product;

        protected ProductFormEvent(ProductForm source, Product product) {
            super(source, false);
            this.product = product;
        }

        public Product getPatience() {
            return product;
        }
    }

    public static class SaveEvent extends ProductFormEvent {
        SaveEvent(ProductForm source, Product product) {
            super(source, product);
        }
    }

    public static class DeleteEvent extends ProductFormEvent {
        DeleteEvent(ProductForm source, Product product) {
            super(source, product);
        }

    }

    public static class CloseEvent extends ProductFormEvent {
        CloseEvent(ProductForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}