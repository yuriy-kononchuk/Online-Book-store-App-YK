INSERT INTO users (id, email, password, first_name, last_name)
VALUES(3, 'testuser@test.com', 'userone', 'User', 'Test');
INSERT INTO users_roles (user_id, role_id) VALUES(3, 2);
INSERT INTO shopping_cart (id, user_id) VALUES(2, 3);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, categories)
VALUES(1, 'FirstBook', 'Mr.First', '555-111', 49.99, 'about first book', 'first cover', 'motivation');
INSERT INTO books (id, title, author, isbn, price, description, cover_image, categories)
VALUES(2, 'SecondBook', 'Mr.Second', '555-222', 39.99, 'about second book', 'second cover', 'history');
INSERT INTO books (id, title, author, isbn, price, description, cover_image, categories)
VALUES(3, 'ThirdBook', 'Mr.Third', '555-333', 59.99, 'about third book', 'third cover', 'motivation');

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity) VALUES(1, 2, 1, 1);
INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity) VALUES(2, 2, 2, 2);
INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity) VALUES(3, 2, 3, 3);
