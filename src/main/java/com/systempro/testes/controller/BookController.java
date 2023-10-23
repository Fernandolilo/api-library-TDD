package com.systempro.testes.controller;

import com.systempro.testes.domain.dto.BookDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/books")
public class BookController {

    @PostMapping
    public BookDTO create() {
        return null;
    }
}
