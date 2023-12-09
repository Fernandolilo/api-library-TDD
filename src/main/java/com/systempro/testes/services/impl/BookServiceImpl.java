package com.systempro.testes.services.impl;

import com.systempro.testes.domain.Book;
import com.systempro.testes.exceptions.BusinessException;
import com.systempro.testes.repositories.BookRepository;
import com.systempro.testes.services.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }
    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("ISBN duplicate");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if(book == null|| book.getId() == null){
            throw new IllegalArgumentException("ID inexistente");
        }
        this.repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if(book == null|| book.getId() == null){
            throw new IllegalArgumentException("ID inexistente");
        }
        return this.repository.save(book);
    }
}
