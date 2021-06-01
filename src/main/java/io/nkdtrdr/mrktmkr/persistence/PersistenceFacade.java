package io.nkdtrdr.mrktmkr.persistence;

import io.nkdtrdr.mrktmkr.dto.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;


@Component
public class PersistenceFacade {
    private final OrderRepository orderRepository;

    public PersistenceFacade(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void trackOrder(Order order) {
        if (orderRepository.existsById(order.getOrderId())) {
            //noinspection OptionalGetWithoutIsPresent
            final Order existingOrder = orderRepository.findById(order.getOrderId()).get();
            order.setQuantity(existingOrder.getQuantity().add(order.getQuantity()));
        }
        orderRepository.save(order);
    }

    public void stopTrackingOrderId(String orderId) {
        orderRepository.deleteById(orderId);
    }

    public Collection<Order> getAllOrders() {
        final ArrayList<Order> result = new ArrayList<>();
        final Iterable<Order> all = orderRepository.findAll();
        all.forEach(result::add);
        return result;
    }
}
