# Book Store, The Online Shop

[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)
[![Mentioned in Awesome Awesome README](https://awesome.re/mentioned-badge-flat.svg)](https://github.com/matiassingers/awesome-readme)

## Table of contents
* [Introduction](#introduction)
* [Project description](#project-description)
* [Project tools & technologies](#project-tools-and-technologies)
* [Provided controller's functionalities](#provided-controllers-functionalities)
* [Test's functionalities](#tests-functionalities)
* [Install and Run the application](#install-and-run-the-application)
* [Usage with Postman](#usage-with-postman)
* [Contacts](#contacts)

## Introduction

In today's dynamic world, where the availability of modern technologies for a quick start and effective development of a business idea in a short period of time is one of the key criteria for making a decision to
enter the market and business. That is why having an online store as a marketplace to promote your own product and ideas as a modern technology of communication with consumers is a necessary tool for a quick start and business
development to the desired scale in market. The development of this application is an example of the accessibility of such a business tool as an online bookstore.

## Project description
**In this app is implemented Book Store an online shop and therefore it has the following domain models :**

**User** : Contains information about the registered user including their authentication details and personal information.
**Role** : Represents the role of a user in the system, can operate in such roles as *USER*, *ADMIN*.
**Book** : Represents a book available in the store.
**Category** : Represents a category that a book can belong to.
**ShoppingCart** : Represents a user's shopping cart.
**CartItem**: Represents an item or items in a user's shopping cart.
**Order** : Represents an order or orders placed by a user.
**OrderItem** : Represents an items in a user's order.

**People involved :**
**Shopper (User)**: Someone who looks at books, puts them in a shopping cart, and buys them.
**Manager (Admin)**: Someone who arranges the books on the store's shelf, watches what gets bought, manages the books and orders.

**Actions Shoppers Can Do**:
- **Join and sign in**: Join the store; Sign in to look at books and buy them.
- **Look at and search for books**: Look at all the books or at one specific book; Find a book by typing its params (etc. title, author, number).
- **Look at bookshelf sections**: See all bookshelf sections; See all books in one section.
- **Use the basket(shopping cart)**: Put a book in the basket; Look inside the basket; Take a book out of the basket.
- **Buying books**: Buy all the books in the basket by creating an order; Look at past order/orders.
- **Look at orders**: See all books on one order; Look closely at one book on an order.
  
**Actions Managers Can Do**:
- **Arrange books**: Add a new book to the store; Change details of a book; Remove a book from the store.
- **Organize bookshelf sections (categories0**: Make a new bookshelf section; Change details of a section; Remove a section.
- **Look at and change receipts**: Change the status of an order, like "Pending", "Delivering" or "Completed"

## Project tools & technologies
This Book Store is a stand-alone **Spring based** application and uses **Spring Boot** extension. In context of *SpringBoot* also were uses **Spring Boot Starters, Spring Boot DevTools** modules. To optimize
data transfer for domain models (entities) within the application and secure API response were used **DTO** objects and also *Mappers* for appropriate data conversion. Ability to use **MapStruct** library and its annotations also provided.
The framework **Spring MVC** with its essential annotations ensures the creation and operation of key controllers. The *Spring MVC validation system* is also used to verify user input and data validation for existing DTO-classes. The architecture of this project is provided by **Spring Data JPA**
implementation and uses the tools necessary to work with the database. In this context also were used **Spring Data JPA Specifications** to provide the ability of creation custom queries like *search books by parameters*.
Implementation of **Spring Boot Security** ensures security-related functionality of app. It is the basis for the operation of such services as *Registration of a new user* and *Login* a user as well as available actions based on the user's role and
non authenticated users in this app. To handle exceptions at the global level the **GlobalExceptionHandler** class is implemented which handles *RegistrationException, EntityNotFoundException, DataNotFoundException* classes at the stages of application operation.
To reduce the number of template code and readability the annotations of **Lombok** library were used. **Liquibase** library is implemented for managing and applying database schema changes and already has preinstalled data sets for using the app and its functionalities.
Possibility of using **Swagger UI** for the existing controllers is provided.

## Provided controller's functionalities
The following controllers ensure the operation of the application and its functions :

- **AuthenticationController** allows you to : **Register a new user** with role "User" and creating an empty shopping cart, can throw *RegistrationException*; **Login** user by email and password ;
- **BookController** is available with 6 functions as : **Create a new book**, **Delete book by ID**, **Update book by ID** are available for *ADMIN* user, **Get all books** into list with pagination and sorting, **Get book by ID**, **Get all books by search parameters** like authors, titles, isbn-numbers
 and still has ability to extend with new added params/classes into code. In case of wrong requested IDs the *EntityNotFoundException* can be thrown ;
- **CategoryController** is available with 6 functions as : **Create a new category**, **Delete category by ID**, **Update category by ID** are available for *ADMIN* user, **Get all categories** into list with pagination and sorting, **Get category by ID**, **Get all books by category ID** into list of books available.
 In case of wrong requested IDs the *EntityNotFoundException* can be thrown ;
- **ShoppingCartController** is available with 4 functions as : **Add book to shopping cart** adds a selected book by id and desired quantity to shopping cart or in case of already existing book adds new quantity, **Update book quantity** for selected book by id and desired new quantity in shopping cart,
 **Delete book by ID** from shopping cart, **Get all items** from shopping cart into list with pagination and sorting. In case of wrong requested IDs the *EntityNotFoundException* can be thrown ;
- **OrderController** is available with 3 functions as : **Save order** creates a new user's order from all shopping cart items and requested shipping address, saves order status as *"Created"*, calculates an order's *Total*, resets shoppincart at the same time. In case of empty shopping cart the *DataNotFoundException* can be thrown ;
 **Get all user's orders** into list with pagination and sorting, **Update status order** is available for *ADMIN* user as the order is processed to another status like *"Pending", "Delivering", "Completed"*. In case of wrong requested order ID the *EntityNotFoundException* can be thrown ;
- **OrderItemController** allows you to : **Get all items** into list with pagination and sorting by order ID, **Get item by ID and with order ID**. In case of wrong requested IDs the *EntityNotFoundException* can be thrown ;
 
## Test's functionalities
**In the process of developing this application, the following tests were developed to check the operation of the following levels and controllers :**
- **Service** : *BookService, CategoryService, CartItemService, ShoppingCartService*  - 33 tests in total ;
- **Repository** : *BookRepository, CartItemRepository, ShoppingCartRepository*  - 6 tests in total. Running these tests is possible while *Docker* is running ;
- **Controller** : *BookController, CategoryController, ShoppingCartController*  - 19 tests in total. Running these tests is possible while *Docker* is running .

## Install and Run the application
To install and run this project you will need to have on your machine : **MySql** database, [**Maven**](https://maven.apache.org), [**Docker**](https://www.docker.com) *(optionally)* and [**Postman**]( https://www.postman.com).

**Getting started** this Spring Boot app is possible in 2 ways : **Locally**  from command line prompt or using a **Docker** within an insulated container.
### Running from command line
1.	Connect to and Log in as a MySQL user from command line ```mysql – u root(or enter user_name) -p``` and enter your password;
2.	Create MySQL Schema for this app with a command : ```mysql> CREATE DATABASE book_store_app;``` ;
3.	*Note.* The application is running on local port ```3306```, make sure it is available;
4.	Make sure that **Maven** is already installed using command line : ```mvn -v``` command. And if it is not you need to install it first;
5.	In your destination folder with this app  run command ```mvn clean package``` to generate a .jar file;
6.	Change work directory with command ```cd target```and finally Run application with command : ```java -jar project-0.0.1-SNAPSHOT.jar```

### Running with Docker
This app is Docker ready! The **Dockerfile** and **docker-compose.yml** files are available at the root of the project.

**Docker** must be already installed on your PC. When starting the app with Docker, a container for the API and a container for MySQL data base are created.
1.	Connect to and Log in as a MySQL user from command line ```mysql – u root(or enter user_name) -p``` and enter your password;
2.	Create MySQL Schema for this app with a command : ```mysql> CREATE DATABASE book_store_app;``` ;
3.	Make sure that **Maven** is already installed using command line : ```mvn -v``` command. And if it is not you need to install it first;
4.	In your destination folder with this app  run command ```mvn clean package``` to generate a .jar file;
5.	In your root project folder create a text file named ```.env``` with the following :
```
MYSQL_ROOT_PASSWORD=your password to MySQL
MYSQL_DATABASE=book_store_app
DB_USER=root (or your MySQL user_name)
DB_PASSWORD= your password to MySQL
MYSQL_LOCAL_PORT=3308
MYSQL_DOCKER_PORT=3306
SPRING_LOCAL_PORT=8088
SPRING_DOCKER_PORT=8080
```
6.	In your root folder run a command ```docker -compose up``` .

## Usage with Postman
For easy start and using of the app’s features with *Postman* for instance, there are already pre-installed 2 users in *project changelog* for logging in with the following parameters :
```
User with Role ADMIN:
“email” : “admin@example.com”
“password” : “12345678”

User with Role USER:
“email” : “testuser@example.com”
“password” : “12344321”
```
**The following are examples of how to use Postman requests to operate with the functions of application controllers :**

1.	**AuthenticationController** is available at :
   - ```http://localhost:8080/api/auth/registration``` for [register](https://imgur.com/GzrE7HP) a user ;
   - ```http://localhost:8080/api/auth/login``` for [login](https://imgur.com/0qtYG1Q) a user .
2.	**BookController** is available at :
   - ```http://localhost:8080/books``` for [add book](https://imgur.com/U99p20e) , [get all books](https://imgur.com/mr0bp6v) ;
   - ```http://localhost:8080/books/{id}``` where {id} should be replaced with desired id for [get book by id](https://imgur.com/vV01sH1), [update book by id](https://imgur.com/CE5Mh52), [delete book by id](https://imgur.com/YtTiOXU) ;
   - ```http://localhost:8080/books/search``` and add search Parameters in Params Tab (*titles, authors, isbnNumbers* together or separately) for [get books by search params](https://imgur.com/yrN9CRb) .
3.	**CategoryController** is available at :
  - ```http://localhost:8080/books/categories``` for [create category](https://imgur.com/yyIFdER) , [get all categories](https://imgur.com/Z2kZPpF) ;
  - ```http://localhost:8080/books/categories/{id}``` where {id} should be replaced with desired id for [get category by id](https://imgur.com/RMxRcNn) , [update category by id](https://imgur.com/kmMeGrX) , [delete category by id](https://imgur.com/8rUJGGa) ;
  - ```http://localhost:8080/books/categories/{id}/books ``` where {id} should be replaced with desired id for [get books by category id](https://imgur.com/5AZEOv8) .
4.	**ShoppingCartController** is available at :
  - ```http://localhost:8080/cart``` for [add book to shopping cart](https://imgur.com/QC4QqBG) or [add book (and if already exists)](https://imgur.com/Lrn3TGV) , [get all items](https://imgur.com/EzePPKN) ;
  - ```http://localhost:8080/cart/cart-items/{id}``` where {id} should be replaced with desired id for [update book quantity by id](https://imgur.com/whn6vrZ) , [delete book by id](https://imgur.com/zrpHDqJ) .
5.	**OrderController** is available at :
  - ```http://localhost:8080/orders``` for [create an order](https://imgur.com/FHbiuCq) , [get user's orders](https://imgur.com/dBW4TBZ) ;
  - ```http://localhost:8080/orders/{id}``` where {id} should be replaced with desired id for [update status order by id](https://imgur.com/DFfaWNK) .
6.	**OrderItemController** is available at :
  - ```http://localhost:8080/orders/{id}/items``` where {id} should be replaced with desired id for [get all items by order id](https://imgur.com/DFfaWNK) ;
  - ```http://localhost:8080/orders/{id}/items/{id}```  where both {id} should be replaced with desired ids for [get item by id and order id](https://imgur.com/jGOw8vW) .

### Here is [**Loom Video**](https://www.loom.com/share/49f8447dfe2d40c4b68c3498661ac39f?sid=b8be5d61-2662-4e21-b077-7938a5883ac4) to show how the project works .

## Contacts
For any inquiries or questions, feel free to reach out via email:

* **Yuriy Kononchuk** - *Java Developer Student of Mate Academy* - **Email:** [yurkononchuk@gmail.com](mailto:yurkononchuk@gmail.com)
