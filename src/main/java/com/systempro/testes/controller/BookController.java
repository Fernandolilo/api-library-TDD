package com.systempro.testes.controller;

import com.systempro.testes.domain.Book;
import com.systempro.testes.domain.dto.BookDTO;
import com.systempro.testes.exceptions.ApiErrors;
import com.systempro.testes.services.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.naming.Binding;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/books")
public class BookController {
    private final BookService service;
    private final ModelMapper modelMapper;

    public BookController(BookService service,
                          ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        Book entity =
               modelMapper.map(dto, Book.class);
        entity = service.save(entity);
        return modelMapper.map(entity, BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidExceptions(MethodArgumentNotValidException exception){
       BindingResult bindingResult = exception.getBindingResult();
        return new ApiErrors(bindingResult);
    }
}
