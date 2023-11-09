package com.systempro.testes.services;

import com.systempro.testes.domain.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Book book);
}
