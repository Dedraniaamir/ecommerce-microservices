package com.msproj.orderservice.repository;

import com.msproj.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List; /**
 * Order Item Repository
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByProductId(Long productId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.customerId = :customerId AND oi.productId = :productId")
    List<OrderItem> findByCustomerAndProduct(@Param("customerId") Long customerId,
                                             @Param("productId") Long productId);

    @Query("SELECT oi.productId, SUM(oi.quantity) FROM OrderItem oi WHERE oi.order.status = 'DELIVERED' GROUP BY oi.productId ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findPopularProducts();
}
