package io.nkdtrdr.mrktmkr.persistence;

import io.nkdtrdr.mrktmkr.dto.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends CrudRepository<Order, String> {
}
