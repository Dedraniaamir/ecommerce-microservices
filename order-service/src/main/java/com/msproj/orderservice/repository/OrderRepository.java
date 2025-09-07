package com.msproj.orderservice.repository;

import com.msproj.orderservice.dto.OrderSummaryDto;
import com.msproj.orderservice.entity.Order;
import com.msproj.orderservice.entity.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Order Repository with advanced JPA features
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Basic queries
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerEmail(String customerEmail);

    // Pessimistic locking for concurrent access control
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithLock(@Param("id") Long id);

    // Date range queries
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    // Amount-based queries
    @Query("SELECT o FROM Order o WHERE o.finalAmount >= :minAmount ORDER BY o.finalAmount DESC")
    List<Order> findHighValueOrders(@Param("minAmount") BigDecimal minAmount);

    // Complex joins with order items
    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.orderItems oi WHERE oi.productId = :productId")
    List<Order> findOrdersContainingProduct(@Param("productId") Long productId);

    // Aggregate queries
    @Query("SELECT COUNT(o) FROM Order o WHERE o.customerId = :customerId")
    Long countOrdersByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.customerId = :customerId AND o.status = 'DELIVERED'")
    BigDecimal calculateTotalSpentByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT o.status, COUNT(o), AVG(o.finalAmount) FROM Order o GROUP BY o.status")
    List<Object[]> getOrderStatsByStatus();

    // Search with filters
    @Query("SELECT o FROM Order o WHERE " +
            "(:customerId IS NULL OR o.customerId = :customerId) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:fromDate IS NULL OR o.orderDate >= :fromDate) AND " +
            "(:toDate IS NULL OR o.orderDate <= :toDate) AND " +
            "(:minAmount IS NULL OR o.finalAmount >= :minAmount) AND " +
            "(:maxAmount IS NULL OR o.finalAmount <= :maxAmount) " +
            "ORDER BY o.orderDate DESC")
    List<Order> findOrdersWithFilters(
            @Param("customerId") Long customerId,
            @Param("status") OrderStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);

    // Modifying queries
    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = 'CANCELLED' WHERE o.status = 'PENDING' AND o.orderDate < :cutoffDate")
    int cancelStaleOrders(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.trackingNumber = :trackingNumber WHERE o.id = :orderId")
    int updateTrackingNumber(@Param("orderId") Long orderId, @Param("trackingNumber") String trackingNumber);

    // Performance queries with specific fetching
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.customerId = :customerId ORDER BY o.orderDate DESC")
    List<Order> findByCustomerIdWithItems(@Param("customerId") Long customerId);

    // Projection queries for performance
    @Query("SELECT new com.msproj.orderservice.dto.OrderSummaryDto(o.id, o.customerId, o.customerName, o.orderDate, o.status, o.finalAmount, SIZE(o.orderItems), o.trackingNumber) FROM Order o WHERE o.customerId = :customerId")
    List<OrderSummaryDto> findOrderSummariesByCustomer(@Param("customerId") Long customerId);
}

