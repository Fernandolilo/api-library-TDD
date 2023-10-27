package com.systempro.testes.controller;

import com.systempro.testes.domain.Book;
import com.systempro.testes.domain.dto.BookDTO;
import com.systempro.testes.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/books")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO dto) {
        Book entity =
                Book.builder()
                        .autor(dto.getAutor())
                        .title(dto.getTitle())
                        .isbn(dto.getIsbn())
                        .build();
        entity = service.save(entity);

        return BookDTO.builder()
                .id(entity.getId())
                .autor(entity.getAutor())
                .title(entity.getTitle())
                .isbn(entity.getIsbn())
                .build();
    }
}
