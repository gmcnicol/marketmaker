package io.nkdtrdr.mrktmkr.persistence;

import io.nkdtrdr.mrktmkr.dto.Order;
import org.springframework.stereotype.Component;

import java.util.Collection;


@Component
public class PersistenceFacade {
    private final OrderRepository orderRepository;

    public PersistenceFacade(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void trackOrder(Order order) {
        orderRepository.save(order);
    }

    public void stopTrackingOrderId(String orderId) {
        orderRepository.findOrdersByOrderIdEquals(orderId).forEach(order -> {
            order.setOrderStatus("complete");
            orderRepository.save(order);
        });
    }

    public Collection<Order> getAllOrders() {
        return orderRepository.findOrdersByOrderStatusIsNull();
    }
}
