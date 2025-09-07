-- Order Service Initial Data

-- Insert sample orders
INSERT INTO orders (customer_id, customer_name, customer_email, order_date, status,
                    total_amount, tax_amount, shipping_amount, discount_amount, final_amount,
                    payment_method, payment_transaction_id, notes,
                    ship_to_name, ship_street_address, ship_city, ship_state, ship_postal_code, ship_country,
                    ship_phone,
                    bill_to_name, bill_street_address, bill_city, bill_state, bill_postal_code, bill_country,
                    created_at, updated_at)
VALUES
-- Order 1: Delivered order
(2, 'John Doe', 'john.doe@email.com', '2024-01-15 10:30:00', 'DELIVERED',
 1299.99, 110.50, 0.00, 130.00, 1280.49,
 'CREDIT_CARD', 'TXN_1705123456_7890', 'First order from John',
 'John Doe', '123 Main St', 'New York', 'NY', '10001', 'USA', '+1234567890',
 'John Doe', '123 Main St', 'New York', 'NY', '10001', 'USA',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Order 2: Processing order
(3, 'Jane Smith', 'jane.smith@email.com', '2024-01-20 14:15:00', 'PROCESSING',
 899.99, 76.50, 9.99, 90.00, 896.48,
 'PAYPAL', 'TXN_1705456789_1234', 'Jane second purchase',
 'Jane Smith', '456 Oak Ave', 'Los Angeles', 'CA', '90210', 'USA', '+1987654321',
 'Jane Smith', '456 Oak Ave', 'Los Angeles', 'CA', '90210', 'USA',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Order 3: Shipped order
(2, 'John Doe', 'john.doe@email.com', '2024-01-22 09:45:00', 'SHIPPED',
 249.97, 21.25, 9.99, 0.00, 281.21,
 'DEBIT_CARD', 'TXN_1705567890_5678', 'Books and accessories',
 'John Doe', '123 Main St', 'New York', 'NY', '10001', 'USA', '+1234567890',
 'John Doe', '123 Main St', 'New York', 'NY', '10001', 'USA',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Order 4: Pending order
(4, 'Bob Wilson', 'bob.wilson@email.com', '2024-01-23 16:20:00', 'PENDING',
 49.99, 4.25, 9.99, 0.00, 64.23,
 'CREDIT_CARD', NULL, 'Pending payment processing',
 'Bob Wilson', '789 Pine St', 'Chicago', 'IL', '60601', 'USA', '+1122334455',
 'Bob Wilson', '789 Pine St', 'Chicago', 'IL', '60601', 'USA',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Order 5: Cancelled order
(3, 'Jane Smith', 'jane.smith@email.com', '2024-01-21 11:30:00', 'CANCELLED',
 199.99, 17.00, 0.00, 0.00, 216.99,
 'CREDIT_CARD', 'TXN_1705234567_9876', 'Cancelled due to stock issue',
 'Jane Smith', '456 Oak Ave', 'Los Angeles', 'CA', '90210', 'USA', '+1987654321',
 'Jane Smith', '456 Oak Ave', 'Los Angeles', 'CA', '90210', 'USA',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert order items
INSERT INTO order_items (order_id, product_id, product_name, product_sku, unit_price, quantity, subtotal,
                         discount_amount,
                         created_at, updated_at)
VALUES
-- Order 1 items (MacBook)
(1, 2, 'MacBook Air M2', 'MBA2M2001', 1299.99, 1, 1299.99, 0.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Order 2 items (Samsung Galaxy)
(2, 3, 'Samsung Galaxy S24', 'SGS24001', 899.99, 1, 899.99, 0.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Order 3 items (Multiple items)
(3, 7, 'Java Programming Book', 'JPB001', 59.99, 1, 59.99, 0.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 8, 'Wireless Headphones', 'WH001', 199.99, 1, 189.98, 10.01, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Order 4 items (Shirt)
(4, 5, 'Mens Formal Shirt', 'MFS001', 49.99, 1, 49.99, 0.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Order 5 items (Headphones - cancelled)
(5, 8, 'Wireless Headphones', 'WH001', 199.99, 1, 199.99, 0.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Update specific order details for shipped/delivered orders
UPDATE orders
SET shipped_date    = '2024-01-16 08:00:00',
    tracking_number = 'TRK1705234567890123'
WHERE id = 1;
UPDATE orders
SET delivered_date = '2024-01-18 14:30:00'
WHERE id = 1;

UPDATE orders
SET shipped_date    = '2024-01-23 10:15:00',
    tracking_number = 'TRK1705567890123456'
WHERE id = 3;