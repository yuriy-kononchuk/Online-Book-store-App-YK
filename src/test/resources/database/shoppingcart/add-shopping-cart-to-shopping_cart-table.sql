INSERT INTO users (id, email, password, first_name, last_name, shopping_cart)
VALUES(5, 'userone@test.com', 'userone', 'User', 'One', 3);
INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity) VALUES(5, 3, 5, 3);
INSERT INTO shopping_cart (id, user_id) VALUES(3, 5);

