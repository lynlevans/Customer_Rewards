DROP TABLE IF EXISTS Transaction;  

CREATE TABLE Transaction (  
    id INT(8) AUTO_INCREMENT PRIMARY KEY,
    customer_id INT(8) NOT NULL,
    date TIMESTAMP NOT NULL,
    cost NUMERIC(20, 2) NOT NULL
);  
