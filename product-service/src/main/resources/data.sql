-- Product Service Initial Data
-- Demonstrates SQL and Collections setup

-- Insert Categories (tree structure)
INSERT INTO categories (name, description, parent_id, image_url, created_at, updated_at)
VALUES ('Electronics', 'Electronic devices and gadgets', NULL, 'https://example.com/electronics.jpg', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('Computers', 'Computer hardware and accessories', 1, 'https://example.com/computers.jpg', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('Mobile Phones', 'Smartphones and mobile accessories', 1, 'https://example.com/phones.jpg', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('Clothing', 'Fashion and apparel', NULL, 'https://example.com/clothing.jpg', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('Men Clothing', 'Clothing for men', 4, 'https://example.com/men.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Women Clothing', 'Clothing for women', 4, 'https://example.com/women.jpg', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('Books', 'Books and educational materials', NULL, 'https://example.com/books.jpg', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- Insert Category Tags (ElementCollection)
INSERT INTO category_tags (category_id, tag)
VALUES (1, 'technology'),
       (1, 'gadgets'),
       (1, 'digital'),
       (2, 'hardware'),
       (2, 'computing'),
       (2, 'office'),
       (3, 'mobile'),
       (3, 'communication'),
       (3, 'portable'),
       (4, 'fashion'),
       (4, 'style'),
       (4, 'apparel'),
       (5, 'mens'),
       (5, 'masculine'),
       (5, 'formal'),
       (6, 'womens'),
       (6, 'feminine'),
       (6, 'trendy'),
       (7, 'education'),
       (7, 'learning'),
       (7, 'knowledge');

-- Insert Products
INSERT INTO products (name, description, price, stock_quantity, sku, status, category_id, created_at, updated_at)
VALUES ('iPhone 15 Pro', 'Latest Apple smartphone with advanced features', 999.99, 50, 'IPH15PRO001', 'ACTIVE', 3,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('MacBook Air M2', 'Lightweight laptop with M2 chip', 1299.99, 25, 'MBA2M2001', 'ACTIVE', 2, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('Samsung Galaxy S24', 'Android flagship smartphone', 899.99, 30, 'SGS24001', 'ACTIVE', 3, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('Dell XPS 13', 'Premium ultrabook laptop', 1199.99, 15, 'DXPS13001', 'ACTIVE', 2, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('Mens Formal Shirt', 'Professional dress shirt', 49.99, 100, 'MFS001', 'ACTIVE', 5, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('Womens Summer Dress', 'Casual summer dress', 79.99, 75, 'WSD001', 'ACTIVE', 6, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('Java Programming Book', 'Complete guide to Java programming', 59.99, 200, 'JPB001', 'ACTIVE', 7,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Wireless Headphones', 'Bluetooth noise-canceling headphones', 199.99, 40, 'WH001', 'ACTIVE', 1,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Gaming Mouse', 'High-precision gaming mouse', 89.99, 60, 'GM001', 'ACTIVE', 2, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('Smartphone Case', 'Protective case for smartphones', 24.99, 150, 'SPC001', 'ACTIVE', 3, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- Insert Product Tags (ElementCollection - demonstrates SET collection)
INSERT INTO product_tags (product_id, tag)
VALUES
-- iPhone tags
(1, 'smartphone'),
(1, 'apple'),
(1, 'ios'),
(1, 'premium'),
(1, 'camera'),
-- MacBook tags
(2, 'laptop'),
(2, 'apple'),
(2, 'portable'),
(2, 'professional'),
(2, 'm2'),
-- Samsung Galaxy tags
(3, 'smartphone'),
(3, 'android'),
(3, 'samsung'),
(3, 'camera'),
(3, 'flagship'),
-- Dell XPS tags
(4, 'laptop'),
(4, 'dell'),
(4, 'ultrabook'),
(4, 'business'),
(4, 'portable'),
-- Shirt tags
(5, 'clothing'),
(5, 'formal'),
(5, 'business'),
(5, 'cotton'),
(5, 'mens'),
-- Dress tags
(6, 'clothing'),
(6, 'casual'),
(6, 'summer'),
(6, 'fashion'),
(6, 'womens'),
-- Book tags
(7, 'education'),
(7, 'programming'),
(7, 'java'),
(7, 'technical'),
(7, 'reference'),
-- Headphones tags
(8, 'audio'),
(8, 'wireless'),
(8, 'bluetooth'),
(8, 'music'),
(8, 'entertainment'),
-- Gaming Mouse tags
(9, 'gaming'),
(9, 'computer'),
(9, 'peripheral'),
(9, 'precision'),
(9, 'esports'),
-- Phone Case tags
(10, 'accessory'),
(10, 'protection'),
(10, 'mobile'),
(10, 'durable'),
(10, 'style');

-- Insert Product Attributes (ElementCollection - demonstrates MAP collection)
INSERT INTO product_attributes (product_id, attribute_name, attribute_value)
VALUES
-- iPhone attributes
(1, 'color', 'Space Gray'),
(1, 'storage', '256GB'),
(1, 'display', '6.1 inch'),
(1, 'os', 'iOS 17'),
-- MacBook attributes
(2, 'color', 'Silver'),
(2, 'ram', '16GB'),
(2, 'storage', '512GB'),
(2, 'processor', 'M2'),
-- Samsung Galaxy attributes
(3, 'color', 'Phantom Black'),
(3, 'storage', '128GB'),
(3, 'display', '6.2 inch'),
(3, 'os', 'Android 14'),
-- Dell XPS attributes
(4, 'color', 'Platinum Silver'),
(4, 'ram', '16GB'),
(4, 'storage', '1TB'),
(4, 'processor', 'Intel i7'),
-- Shirt attributes
(5, 'size', 'L'),
(5, 'color', 'Blue'),
(5, 'material', 'Cotton'),
(5, 'fit', 'Slim'),
-- Dress attributes
(6, 'size', 'M'),
(6, 'color', 'Red'),
(6, 'material', 'Cotton Blend'),
(6, 'style', 'A-Line'),
-- Book attributes
(7, 'author', 'Oracle Press'),
(7, 'pages', '800'),
(7, 'edition', '12th'),
(7, 'language', 'English'),
-- Headphones attributes
(8, 'color', 'Black'),
(8, 'type', 'Over-Ear'),
(8, 'battery', '30 hours'),
(8, 'noise_canceling', 'Yes'),
-- Mouse attributes
(9, 'color', 'Black'),
(9, 'dpi', '16000'),
(9, 'buttons', '8'),
(9, 'wireless', 'Yes'),
-- Case attributes
(10, 'color', 'Clear'),
(10, 'material', 'TPU'),
(10, 'protection', 'Drop-proof'),
(10, 'compatibility', 'Universal');

-- Insert Product Images (ElementCollection - demonstrates LIST collection with order)
INSERT INTO product_images (product_id, image_url, image_order)
VALUES
-- iPhone images
(1, 'https://example.com/iphone15pro_1.jpg', 0),
(1, 'https://example.com/iphone15pro_2.jpg', 1),
(1, 'https://example.com/iphone15pro_3.jpg', 2),
-- MacBook images
(2, 'https://example.com/macbook_air_1.jpg', 0),
(2, 'https://example.com/macbook_air_2.jpg', 1),
-- Samsung Galaxy images
(3, 'https://example.com/galaxy_s24_1.jpg', 0),
(3, 'https://example.com/galaxy_s24_2.jpg', 1),
-- Dell XPS images
(4, 'https://example.com/dell_xps_1.jpg', 0),
-- Shirt images
(5, 'https://example.com/formal_shirt_1.jpg', 0),
(5, 'https://example.com/formal_shirt_2.jpg', 1),
-- Dress images
(6, 'https://example.com/summer_dress_1.jpg', 0),
-- Book images
(7, 'https://example.com/java_book_1.jpg', 0),
-- Headphones images
(8, 'https://example.com/headphones_1.jpg', 0),
(8, 'https://example.com/headphones_2.jpg', 1),
-- Mouse images
(9, 'https://example.com/gaming_mouse_1.jpg', 0),
-- Case images
(10, 'https://example.com/phone_case_1.jpg', 0);

-- Insert Product Reviews
INSERT INTO product_reviews (product_id, customer_id, customer_name, rating, title, comment, verified_purchase,
                             created_at, updated_at)
VALUES (1, 2, 'John Doe', 5, 'Excellent phone!', 'Great camera quality and performance. Highly recommended!', true,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (1, 3, 'Jane Smith', 4, 'Good but pricey', 'Nice features but quite expensive for what it offers.', true,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 2, 'John Doe', 5, 'Perfect for work', 'Lightweight and powerful. Perfect for software development.', true,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 3, 'Jane Smith', 4, 'Great Android phone', 'Good alternative to iPhone with excellent display.', false,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (7, 2, 'John Doe', 5, 'Comprehensive Java guide',
        'Excellent book for learning Java. Very detailed explanations.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (8, 3, 'Jane Smith', 4, 'Good sound quality', 'Comfortable to wear and good noise cancellation.', true,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);