package com.example.project;

import com.example.project.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineBookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    /* @Bean //This is to remove CommandLineRunner bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("New Project");
            book.setAuthor("Yuriy Kononchuk");
            book.setIsbn("123-456-7890-00-5");
            book.setPrice(BigDecimal.valueOf(49.99));
            //bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }*/
}
