package com.example.application.data.service;

import com.example.application.data.entity.OrderItem;
import com.example.application.data.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;


    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;

    }

    public void flush(){
        orderItemRepository.flush();
    }

    public OrderItem saveAndFlush(OrderItem item){
        return orderItemRepository.saveAndFlush(item);
    }
    public OrderItem findById(Long id){

       return  orderItemRepository.findById(id).get();

    }

    public List<OrderItem> findAllPatience() {
            return orderItemRepository.findAll();

    }


    public void deleteOrderItem(OrderItem item) {
        orderItemRepository.delete(item);
    }

    public void saveOrderItem(OrderItem item) {
        if (item == null) {
            System.err.println("OrderItem is null. Are you sure you have connected your form to the application?");
            return;
        }
        orderItemRepository.save(item);
    }

}
