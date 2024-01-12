package com.example.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.project.dto.book.BookDto;
import com.example.project.dto.book.BookDtoWithoutCategoryIds;
import com.example.project.dto.book.BookSearchParameters;
import com.example.project.dto.book.CreateBookRequestDto;
import com.example.project.mapper.BookMapper;
import com.example.project.model.Book;
import com.example.project.model.Category;
import com.example.project.repository.book.BookRepository;
import com.example.project.repository.book.BookSpecificationBuilder;
import com.example.project.service.impl.BookServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Verify save() method works correctly")
    void save_ValidCreateBookRequestDto_ReturnsBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Ukraine wins");
        requestDto.setAuthor("Big Boss");
        requestDto.setIsbn("555-5-23-230872-5");
        requestDto.setPrice(BigDecimal.valueOf(49.99));
        requestDto.setDescription("victory for Ukraine");
        requestDto.setCoverImage("Best buy");
        requestDto.setCategoryIds(List.of(1L, 2L));

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("motivational");
        category1.setDescription("motivation and self-development");
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("historical");
        category1.setDescription("history researches");

        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());
        book.setCategories(Set.of(category1, category2));

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategoryIds(List.of(1L, 2L));

        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto savedBookDto = bookService.save(requestDto);

        AssertionsForClassTypes.assertThat(savedBookDto).isEqualTo(bookDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify findAll() method works correctly")
    void findAll_ValidPageable_ReturnsAllBookDtos() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Ukraine wins");
        book1.setAuthor("Zaluzhnyj");
        book1.setIsbn("555-5-23-230872-5");
        book1.setPrice(BigDecimal.valueOf(49.99));
        book1.setDescription("victory for Ukraine");
        book1.setCoverImage("Best buy");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Our Ukraine");
        book2.setAuthor("Big Boss");
        book2.setIsbn("555-5-23-230872-7");
        book2.setPrice(BigDecimal.valueOf(69.99));
        book2.setDescription("Amazing Ukraine");
        book2.setCoverImage("Best countries");

        BookDtoWithoutCategoryIds bookDto1WithoutCategoryIds = new BookDtoWithoutCategoryIds();
        bookDto1WithoutCategoryIds.setId(book1.getId());
        bookDto1WithoutCategoryIds.setTitle(book1.getTitle());
        bookDto1WithoutCategoryIds.setAuthor(book1.getAuthor());
        bookDto1WithoutCategoryIds.setIsbn(book1.getIsbn());
        bookDto1WithoutCategoryIds.setPrice(book1.getPrice());
        bookDto1WithoutCategoryIds.setDescription(book1.getDescription());
        bookDto1WithoutCategoryIds.setCoverImage(book1.getCoverImage());

        BookDtoWithoutCategoryIds bookDto2WithoutCategoryIds = new BookDtoWithoutCategoryIds();
        bookDto2WithoutCategoryIds.setId(book2.getId());
        bookDto2WithoutCategoryIds.setTitle(book2.getTitle());
        bookDto2WithoutCategoryIds.setAuthor(book2.getAuthor());
        bookDto2WithoutCategoryIds.setIsbn(book2.getIsbn());
        bookDto2WithoutCategoryIds.setPrice(book2.getPrice());
        bookDto2WithoutCategoryIds.setDescription(book2.getDescription());
        bookDto2WithoutCategoryIds.setCoverImage(book2.getCoverImage());

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book1, book2);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategories(book1)).thenReturn(bookDto1WithoutCategoryIds);
        when(bookMapper.toDtoWithoutCategories(book2)).thenReturn(bookDto2WithoutCategoryIds);

        List<BookDtoWithoutCategoryIds> bookDtosWithoutCategoryIds = bookService.findAll(pageable);

        AssertionsForClassTypes.assertThat(bookDtosWithoutCategoryIds.size()).isEqualTo(2);
        AssertionsForClassTypes.assertThat(bookDtosWithoutCategoryIds.get(0))
                .isEqualTo(bookDto1WithoutCategoryIds);
        AssertionsForClassTypes.assertThat(bookDtosWithoutCategoryIds.get(1))
                .isEqualTo(bookDto2WithoutCategoryIds);

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper,times(1)).toDtoWithoutCategories(book1);
        verify(bookMapper,times(1)).toDtoWithoutCategories(book2);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify findById() method works correctly")
    void findById_ValidBookId_ReturnsValidBookDto() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("motivational");
        category1.setDescription("motivation and self-development");
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("historical");
        category1.setDescription("history researches");
        Long bookId = 1L;

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Ukraine wins");
        book.setAuthor("Zaluzhnyj");
        book.setIsbn("555-5-23-230872-5");
        book.setPrice(BigDecimal.valueOf(49.99));
        book.setDescription("victory for Ukraine");
        book.setCoverImage("Best buy");
        book.setCategories(Set.of(category1, category2));

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(bookDto.getAuthor());
        bookDto.setIsbn(bookDto.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(bookDto.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategoryIds(List.of(1L, 2L));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto bookDtoById = bookService.findById(bookId);

        AssertionsForClassTypes.assertThat(bookDtoById).isEqualTo(bookDto);
        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify findById() method with wrong Id throws exception")
    void findById_WithNonValidBookId_ThrowsException() {
        Long bookId = 100L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(bookId));

        String expected = "Can't find a book by id: " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Verify deleteById() method works correctly")
    void deleteById_ValidBookId_BookIsDeleted() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Great victories");
        category1.setDescription("World's greatest victories");
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("historical");
        category1.setDescription("history researches");
        Long bookId = 1L;

        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Old title");
        book.setAuthor("Old Author");
        book.setIsbn("555-5-23-230872-0");
        book.setPrice(BigDecimal.valueOf(9.99));
        book.setDescription("need to delete");
        book.setCoverImage("old cover");
        book.setCategories(Set.of(category1, category2));

        bookService.deleteById(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify deleteById() method with Id is null calls exception")
    void deleteById_WithBookIdIsNull_ThrowsException() {
        Long bookId = null;

        doThrow(IllegalArgumentException.class).when(bookRepository).deleteById(bookId);

        assertThrows(IllegalArgumentException.class, () -> bookService.deleteById(bookId));

        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    @DisplayName("Verify updateById() method works correctly")
    void updateById_ValidBookId_ReturnsBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Ukraine wins");
        requestDto.setAuthor("Big Boss");
        requestDto.setIsbn("555-5-23-230872-5");
        requestDto.setPrice(BigDecimal.valueOf(49.99));
        requestDto.setDescription("victory for Ukraine");
        requestDto.setCoverImage("Best buy");
        requestDto.setCategoryIds(List.of(1L, 2L));

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("motivational");
        category1.setDescription("motivation and self-development");
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("historical");
        category1.setDescription("history researches");
        Long bookId = 1L;

        Book oldBook = new Book();
        oldBook.setId(bookId);
        oldBook.setTitle("Old Name");
        oldBook.setAuthor("Old Author");
        oldBook.setIsbn("555-5-23-230872-0");
        oldBook.setPrice(BigDecimal.valueOf(9.99));
        oldBook.setDescription("Old description");
        oldBook.setCoverImage("Old cover image");
        oldBook.setCategories(Set.of(category1, category2));

        Book updatedBook = new Book();
        updatedBook.setId(bookId);
        updatedBook.setTitle(requestDto.getTitle());
        updatedBook.setAuthor(requestDto.getAuthor());
        updatedBook.setIsbn(requestDto.getIsbn());
        updatedBook.setPrice(requestDto.getPrice());
        updatedBook.setDescription(requestDto.getDescription());
        updatedBook.setCoverImage(requestDto.getCoverImage());
        updatedBook.setCategories(Set.of(category1, category2));

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setId(bookId);
        updatedBookDto.setTitle(updatedBook.getTitle());
        updatedBookDto.setAuthor(updatedBook.getAuthor());
        updatedBookDto.setIsbn(updatedBook.getIsbn());
        updatedBookDto.setPrice(updatedBook.getPrice());
        updatedBookDto.setDescription(updatedBook.getDescription());
        updatedBookDto.setCoverImage(updatedBook.getCoverImage());
        updatedBookDto.setCategoryIds(List.of(1L, 2L));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(oldBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        bookService.updateById(bookId, requestDto);

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(any(Book.class));
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify updateById() method with wrong Id throws exception")
    void updateById_WithNonValidBookId_ThrowsException() {
        Long bookId = 100L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.updateById(bookId, requestDto));

        String expected = "Can't find a book by id: " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Verify findAllByCategoryId() method works correctly")
    void findAllByCategoryId_ValidCategoryId_ReturnsAllBookDtos() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Ukraine wins");
        book1.setAuthor("Zaluzhnyj");
        book1.setIsbn("555-5-23-230872-5");
        book1.setPrice(BigDecimal.valueOf(49.99));
        book1.setDescription("victory for Ukraine");
        book1.setCoverImage("Best buy");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Our Ukraine");
        book2.setAuthor("Big Boss");
        book2.setIsbn("555-5-23-230872-7");
        book2.setPrice(BigDecimal.valueOf(69.99));
        book2.setDescription("Amazing Ukraine");
        book2.setCoverImage("Best countries");

        BookDto bookDto1 = new BookDto();
        bookDto1.setId(book1.getId());
        bookDto1.setTitle(book1.getTitle());
        bookDto1.setAuthor(book1.getAuthor());
        bookDto1.setIsbn(book1.getIsbn());
        bookDto1.setPrice(book1.getPrice());
        bookDto1.setDescription(book1.getDescription());
        bookDto1.setCoverImage(book1.getCoverImage());

        BookDto bookDto2 = new BookDto();
        bookDto2.setId(book2.getId());
        bookDto2.setTitle(book2.getTitle());
        bookDto2.setAuthor(book2.getAuthor());
        bookDto2.setIsbn(book2.getIsbn());
        bookDto2.setPrice(book2.getPrice());
        bookDto2.setDescription(book2.getDescription());
        bookDto2.setCoverImage(book2.getCoverImage());

        Long categoryId = 1L;
        List<Book> books = List.of(book1, book2);

        when(bookRepository.findAllByCategoriesId(categoryId)).thenReturn(books);
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);

        List<BookDto> bookDtos = bookService.findAllByCategoryId(categoryId);

        AssertionsForClassTypes.assertThat(bookDtos.size()).isEqualTo(2);
        AssertionsForClassTypes.assertThat(bookDtos.get(0))
                .isEqualTo(bookDto1);
        AssertionsForClassTypes.assertThat(bookDtos.get(1))
                .isEqualTo(bookDto2);

        verify(bookRepository, times(1)).findAllByCategoriesId(categoryId);
        verify(bookMapper,times(1)).toDto(book1);
        verify(bookMapper,times(1)).toDto(book2);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void searchForBooks() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Ukraine wins");
        book1.setAuthor("Zaluzhnyj");
        book1.setIsbn("555-5-23-230872-5");
        book1.setPrice(BigDecimal.valueOf(49.99));
        book1.setDescription("victory for Ukraine");
        book1.setCoverImage("Best buy");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Our Ukraine");
        book2.setAuthor("Big Boss");
        book2.setIsbn("555-5-23-230872-7");
        book2.setPrice(BigDecimal.valueOf(69.99));
        book2.setDescription("Amazing Ukraine");
        book2.setCoverImage("Best countries");

        BookDto bookDto1 = new BookDto();
        bookDto1.setId(book1.getId());
        bookDto1.setTitle(book1.getTitle());
        bookDto1.setAuthor(book1.getAuthor());
        bookDto1.setIsbn(book1.getIsbn());
        bookDto1.setPrice(book1.getPrice());
        bookDto1.setDescription(book1.getDescription());
        bookDto1.setCoverImage(book1.getCoverImage());

        BookDto bookDto2 = new BookDto();
        bookDto2.setId(book2.getId());
        bookDto2.setTitle(book2.getTitle());
        bookDto2.setAuthor(book2.getAuthor());
        bookDto2.setIsbn(book2.getIsbn());
        bookDto2.setPrice(book2.getPrice());
        bookDto2.setDescription(book2.getDescription());
        bookDto2.setCoverImage(book2.getCoverImage());

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book1, book2);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        String[] titles = {"Ukraine wins"};
        String[] authors = {"Zaluzhnyj"};
        String[] isbnNumbers = {"555-5-23-230872-7"};
        BookSearchParameters searchParameters = new BookSearchParameters(
                titles, authors, isbnNumbers);
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);

        when(bookRepository.findAll(bookSpecification,pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);

        List<BookDto> bookDtos = bookService.searchForBooks(searchParameters, pageable);

        AssertionsForClassTypes.assertThat(bookDtos.size()).isEqualTo(2);
        AssertionsForClassTypes.assertThat(bookDtos.get(0))
                .isEqualTo(bookDto1);
        AssertionsForClassTypes.assertThat(bookDtos.get(1))
                .isEqualTo(bookDto2);

        verify(bookRepository, times(1)).findAll(bookSpecification, pageable);
        verify(bookMapper,times(1)).toDto(book1);
        verify(bookMapper,times(1)).toDto(book2);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }
}
