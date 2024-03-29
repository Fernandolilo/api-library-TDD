package com.systempro.testes.services.impl;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.systempro.testes.domain.Book;
import com.systempro.testes.exceptions.BusinessException;
import com.systempro.testes.repositories.BookRepository;
import com.systempro.testes.services.BookService;

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
	
    @Override
	public Page<Book> find(Book filter, Pageable pageRequest) {
		Example<Book> example =  Example.of(filter, 
				ExampleMatcher
				.matching()
				.withIgnoreCase()
				.withIgnoreNullValues()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
				);
		
		return repository.findAll(example, pageRequest);
	}
	
}
