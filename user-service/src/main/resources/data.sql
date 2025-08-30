-- Initial data for User Service
-- This file demonstrates SQL and will populate initial data

-- Insert default roles
INSERT INTO roles (name, description, created_at, updated_at)
VALUES ('ADMIN', 'Administrator with full access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('CUSTOMER', 'Regular customer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('MODERATOR', 'Content moderator', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('SUPPORT', 'Support agent', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample admin user
INSERT INTO users (user_type, username, email, password, first_name, last_name, status, created_at, updated_at,
                   employee_id, department, admin_level)
VALUES ('ADMIN', 'admin', 'admin@ecommerce.com', 'admin123', 'System', 'Administrator', 'ACTIVE', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'EMP001', 'IT', 'SUPER_ADMIN');

-- Insert sample customers
INSERT INTO users (user_type, username, email, password, first_name, last_name, status, created_at, updated_at,
                   phone_number, loyalty_points, customer_tier, street_address, city, state, postal_code, country)
VALUES ('CUSTOMER', 'john_doe', 'john.doe@email.com', 'password123', 'John', 'Doe', 'ACTIVE', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, '+1234567890', 1500, 'SILVER', '123 Main St', 'New York', 'NY', '10001', 'USA'),
       ('CUSTOMER', 'jane_smith', 'jane.smith@email.com', 'password123', 'Jane', 'Smith', 'ACTIVE', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, '+1987654321', 500, 'BRONZE', '456 Oak Ave', 'Los Angeles', 'CA', '90210', 'USA'),
       ('CUSTOMER', 'bob_wilson', 'bob.wilson@email.com', 'password123', 'Bob', 'Wilson', 'PENDING_VERIFICATION',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '+1122334455', 0, 'BRONZE', NULL, NULL, NULL, NULL, NULL);

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id, is_active, created_at, updated_at)
VALUES (1, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- Admin gets ADMIN role
       (2, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- John gets CUSTOMER role
       (3, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- Jane gets CUSTOMER role
       (4, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- Bob gets CUSTOMER role