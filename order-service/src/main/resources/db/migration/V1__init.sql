-- Create table for Order
CREATE TABLE t_orders (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          order_number VARCHAR(255) NOT NULL
);

-- Create table for OrderLineItems
CREATE TABLE t_order_line_items (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    sku_code VARCHAR(255) NOT NULL,
                                    price DECIMAL(19, 2) NOT NULL,
                                    quantity INT NOT NULL
);

-- Create foreign key relationship between Order and OrderLineItems
ALTER TABLE t_order_line_items
    ADD COLUMN order_id BIGINT,
ADD CONSTRAINT fk_order
FOREIGN KEY (order_id) REFERENCES t_orders(id);
