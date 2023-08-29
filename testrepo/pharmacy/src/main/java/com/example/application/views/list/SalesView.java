package com.example.application.views.list;


import com.example.application.data.entity.Customer;
import com.example.application.data.entity.Orders;
import com.example.application.data.entity.OrderItem;
import com.example.application.data.entity.Product;
import com.example.application.data.service.CustomerService;
import com.example.application.data.service.OrderItemService;
import com.example.application.data.service.OrderService;
import com.example.application.data.service.ProductService;
import com.example.application.util.ResourceBundleUtil;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
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
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


@Component
@Scope("prototype")
@Route(value = "Sales", layout = MainLayout.class)
@PageTitle("Pharmacy")

@PermitAll
public class SalesView extends VerticalLayout {

    private ProductService productService;
    private CustomerService customerService;

    private OrderService orderService;

    private OrderItemService orderItemService;


    ComboBox<Customer> customerCombo;
    ComboBox<Product> productCombo;

    IntegerField quantityField;
    Button addToListButton;

    Button acceptButton;
    String lang;
    ResourceBundleUtil rb;

    Grid<OrderItem> orderItemGrid;

    Orders currentOrder;

    List<OrderItem> orderItems;

    public SalesView(ProductService productService, CustomerService customerService, OrderService orderService, OrderItemService orderItemService) {
        this.productService = productService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;

        lang = VaadinSession.getCurrent().getAttribute("language").toString();
        rb = new ResourceBundleUtil(lang);

        UI.getCurrent().getPage().setTitle(rb.getString("personnelListTitle"));

        addClassName("list-view");
        setSizeFull();

        add(getContent(), addToListButton,acceptButton, orderItemGrid);

    }

    private HorizontalLayout getContent(){

        HorizontalLayout content = new HorizontalLayout();

        List<Customer> customerList = customerService.findAllCustomer();
        customerCombo = new ComboBox<>("Customers");
        customerCombo.setItems(customerList);
        customerCombo.setItemLabelGenerator(customer -> customer.getName() + " " + customer.getLastName());
        customerCombo.addValueChangeListener(event -> resetOrder());

        List<Product> productList = productService.findAllProduct();
        productCombo = new ComboBox<>("Products");
        productCombo.setItems(productList);
        productCombo.setItemLabelGenerator(product -> product.getName());

        quantityField = new IntegerField("Quantity");
        quantityField.setMin(0);

        orderItems = new ArrayList<>();

        orderItemGrid = new Grid<>(OrderItem.class);
        orderItemGrid.addClassNames("order-grid");
        orderItemGrid.setSizeFull();
        orderItemGrid.setColumns("product.name", "quantity", "subTotal");
        orderItemGrid.setItems(orderItems);

        addToListButton = new Button("Add to list");
        addToListButton.addClickListener(event -> addToList());

        acceptButton = new Button("Accept");
        acceptButton.addClickListener(event -> acceptOrder());
        HorizontalLayout layout = new HorizontalLayout(customerCombo,productCombo,quantityField);

        content.add(layout);

        return content;

    }

    private void addToList(){

        if(quantityField.getValue() > productCombo.getValue().getQuantityInStock()){
            Notification.show("Out of stock").setPosition(Notification.Position.MIDDLE);
        }else {

            boolean flag = orderItems
                    .stream()
                    .anyMatch(oi -> oi.getProduct().getProductId() == productCombo.getValue().getProductId());

            if (flag) {
                OrderItem oiToUpdate = orderItems
                        .stream()
                        .filter(oi -> oi.getProduct().getProductId() == productCombo.getValue().getProductId())
                        .toList()
                        .get(0);
                int demandedProductCount = oiToUpdate.getQuantity();
                if(quantityField.getValue() > (oiToUpdate.getProduct().getQuantityInStock()-demandedProductCount)){
                    Notification.show("Out of stock").setPosition(Notification.Position.MIDDLE);
                    return;

                }

                oiToUpdate.setQuantity(oiToUpdate.getQuantity() + quantityField.getValue());
                oiToUpdate.setSubTotal(oiToUpdate.getQuantity() * oiToUpdate.getProduct().getUnitPrice());
            } else {

                OrderItem oi = new OrderItem();

                oi.setProduct(productCombo.getValue());
                oi.setQuantity(quantityField.getValue());
                oi.setSubTotal(quantityField.getValue() * productCombo.getValue().getUnitPrice());

                orderItems.add(oi);
            }
            updateGrid();
        }
    }

    private void updateGrid(){
        orderItemGrid.setItems(orderItems);
    }

    private void acceptOrder(){

        if(orderItems.size() > 0) {

            Orders order = new Orders();

            order.setOrderDate(new Date(System.currentTimeMillis()));
            order.setCustomer(customerCombo.getValue());
            double totalAmount = orderItems.stream().mapToDouble(OrderItem::getSubTotal).sum();
            order.setTotalAmount(totalAmount);
            order.setItems(orderItems);
            //orderItems.forEach(oi -> orderItemService.saveOrderItem(oi));
            orderItems.forEach(oi -> oi.setOrder(order));
            orderService.saveOrder(order);
        }

    }

    private void resetOrder(){

        orderItems = new ArrayList<>();
        updateGrid();


    }



}
