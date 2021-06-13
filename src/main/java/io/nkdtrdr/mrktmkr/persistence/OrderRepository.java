package io.nkdtrdr.mrktmkr.persistence;

import io.nkdtrdr.mrktmkr.dto.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;


@Repository
public interface OrderRepository extends CrudRepository<Order, BigDecimal> {

    void deleteByOrderId(String orderId);
}
