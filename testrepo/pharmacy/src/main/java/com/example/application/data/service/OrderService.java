package com.example.application.data.service;

import com.example.application.data.entity.Orders;
import com.example.application.data.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private OrderItemService orderItemService;


    public OrderService(OrderRepository orderRepository, OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
        this.orderRepository = orderRepository;

    }

    public void flush(){
        orderRepository.flush();
    }

    public Orders saveAndFlush(Orders order){
        return orderRepository.saveAndFlush(order);
    }
    public Orders findById(Long id){

       return  orderRepository.findById(id).get();

    }

    public List<Orders> findAllOrder() {
            return orderRepository.findAll();

    }

    public long countOrder() {
        return orderRepository.count();
    }

    public void deleteOrder(Orders order) {
        orderRepository.delete(order);
    }

    public void saveOrder(Orders order) {
        if (order == null) {
            System.err.println("Order is null. Are you sure you have connected your form to the application?");
            return;
        }

        orderRepository.save(order);
    }

}
