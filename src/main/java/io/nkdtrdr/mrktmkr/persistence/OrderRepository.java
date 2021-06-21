package io.nkdtrdr.mrktmkr.persistence;

import io.nkdtrdr.mrktmkr.dto.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findOrdersByOrderIdEquals(String orderId);

    List<Order> findOrdersByOrderStatusIsNull();
}
