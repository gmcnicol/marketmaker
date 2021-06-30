package io.nkdtrdr.mrktmkr.persistence;

import com.binance.api.client.domain.account.AssetBalance;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.persistence.model.Asset;
import io.nkdtrdr.mrktmkr.persistence.processors.AssetRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class PersistenceFacade {
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;

    public PersistenceFacade(final OrderRepository orderRepository, final AssetRepository assetRepository) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
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

    public void saveAssets(List<AssetBalance> assetBalances) {
        final List<Asset> collect = assetBalances.stream()
                .map(Asset::convert)
                .collect(Collectors.toList());

        assetRepository.saveAll(collect);
    }
}
