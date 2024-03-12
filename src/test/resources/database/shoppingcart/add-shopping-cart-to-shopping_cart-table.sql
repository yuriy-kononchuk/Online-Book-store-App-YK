INSERT INTO users (id, email, password, first_name, last_name)
VALUES(5, 'userone@test.com', 'userone', 'User', 'One');
INSERT INTO books (id, title, author, isbn, price, description, cover_image, categories)
VALUES(5, 'FirstBook', 'Mr.First', '555-111', 49.99, 'about first book', 'first cover', 'motivation');
INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity) VALUES(5, 3, 5, 3);
INSERT INTO shopping_cart (id, user_id) VALUES(3, 5);

