package com.library.dao;

import com.library.model.Book;
import java.util.List;

public interface BookDAO {

    int addBook(Book book);

    boolean updateBook(Book book);

    boolean deleteBook(int bookId);

    Book getBookById(int bookId);

    List<Book> getAllBooks();

    List<Book> searchByTitleOrAuthor(String keyword);
}
