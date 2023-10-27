package com.systempro.testes.services.impl;

import com.systempro.testes.domain.Book;
import com.systempro.testes.repositories.BookRepository;
import com.systempro.testes.services.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }
    @Override
    public Book save(Book book) {
        return repository.save(book);
    }
}
