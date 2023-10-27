package com.systempro.testes.controller;

import com.systempro.testes.domain.dto.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/books")
public class BookController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create() {

        BookDTO dto = new BookDTO();
        dto.setAutor("Fernando");
        dto.setTitle("Meu Livro");
        dto.setIsbn("123456");
        dto.setId(1L);
        return dto;
    }
}
