CREATE TABLE product_offering (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2) NOT NULL
);

INSERT INTO product_offering (id, name, price) 
VALUES ('po-1', 'Cloud Storage 100GB', 9.99);
INSERT INTO product_offering (id, name, price) 
VALUES ('po-2', 'Cloud Storage 500GB', 19.99);
INSERT INTO product_offering (id, name, price) 
VALUES ('po-3', 'Cloud Storage 1TB', 39.99);
INSERT INTO product_offering (id, name, price) 
VALUES ('po-4', 'Cloud Storage 5TB', 199.99);
INSERT INTO product_offering (id, name, price) 
VALUES ('po-5', 'Cloud Storage 10TB', 399.99);
