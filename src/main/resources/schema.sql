CREATE SCHEMA IF NOT EXISTS SCAMMAZON;
SET SCHEMA SCAMMAZON;
DROP TABLE IF EXISTS cart_product;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(250) NOT NULL,
    password VARCHAR(250) NOT NULL
);

CREATE TABLE carts
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_status      VARCHAR(255) NOT NULL,
    total_final_price double,
    user_id          BIGINT,
    CONSTRAINT user_product_fk FOREIGN KEY (user_id) REFERENCES users (id)

);

create TABLE products
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255),
    stock_quantity long,
    minim_items_for_discount long,
    number_of_free_items double,
    sku VARCHAR(255) not null,
    unique(sku)
);

CREATE TABLE cart_product
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity BIGINT NOT NULL,
    CONSTRAINT cart_cart_product_fk FOREIGN KEY (cart_id) REFERENCES carts (id),
    CONSTRAINT product_cart_product_fk FOREIGN KEY (product_id) REFERENCES products (id),
    unique(product_id, product_id)
);




