package nl.trifork.coins.trading.query;

import nl.trifork.coins.coreapi.GetOrderQuery;
import nl.trifork.coins.coreapi.OrderCreatedEvent;
import nl.trifork.coins.coreapi.OrderDto;
import nl.trifork.coins.coreapi.OrderExecutedEvent;
import nl.trifork.coins.coreapi.OrderFailedEvent;
import nl.trifork.coins.coreapi.OrderStatus;
import nl.trifork.coins.coreapi.OrderSuccessEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderProjection {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;


    @EventHandler
    public void on(OrderCreatedEvent event) {
        saveAndEmit(mapOrderEntity(event));
    }

    @EventHandler
    public void on(OrderExecutedEvent event) {
        Optional<OrderEntity> orderEntity = this.orderRepository.findById(event.getId());
        orderEntity.ifPresent(order -> {
            order.setStatus(OrderStatus.PENDING);
            saveAndEmit(order);
        });
    }

    @EventHandler
    public void on(OrderSuccessEvent event) {
        Optional<OrderEntity> orderEntity = this.orderRepository.findById(event.getId());
        orderEntity.ifPresent(order -> {
            order.setStatus(OrderStatus.COMPLETED);
            saveAndEmit(order);
        });

    }

    @EventHandler
    public void on(OrderFailedEvent event) {
        Optional<OrderEntity> orderEntity = this.orderRepository.findById(event.getId());
        orderEntity.ifPresent(order -> {
            order.setStatus(OrderStatus.FAILED);
            saveAndEmit(order);
        });

    }

    @QueryHandler
    public OrderDto getOrder(GetOrderQuery query) {
        Optional<OrderEntity> orderEntity = this.orderRepository.findById(query.getId());
        return orderEntity.map(this::mapEntityToDto).orElseThrow(RuntimeException::new);
    }

    private void saveAndEmit(OrderEntity orderEntity) {
        this.orderRepository.save(orderEntity);
        this.queryUpdateEmitter.emit(GetOrderQuery.class, query -> query.getId().equals(orderEntity.getId()), mapEntityToDto(orderEntity));
    }

    private OrderDto mapEntityToDto(OrderEntity orderEntity) {
        return new OrderDto(orderEntity.getId(), orderEntity.getFromCurrency(), orderEntity.getToCurrency(), orderEntity.getAmount(), orderEntity.getPrice(), orderEntity.getStatus());
    }

    private OrderEntity mapOrderEntity(OrderCreatedEvent event) {
        return new OrderEntity(event.getId(), event.getUserId(), event.getFromCurrency(), event.getToCurrency(), event.getAmount(), event.getPrice(), OrderStatus.CREATED);
    }
}
