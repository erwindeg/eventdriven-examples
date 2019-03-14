package nl.trifork.coins.trading.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
}
