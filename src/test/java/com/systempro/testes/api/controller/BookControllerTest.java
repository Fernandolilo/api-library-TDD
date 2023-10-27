package com.systempro.testes.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systempro.testes.domain.Book;
import com.systempro.testes.domain.dto.BookDTO;
import com.systempro.testes.services.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest

@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/books";

    @Autowired
    MockMvc mvc;
    @MockBean
    BookService service;

    @Test
    @DisplayName("Created Book")
    public void createBookTest() throws Exception {
        BookDTO dto = BookDTO
                .builder()
                .autor("Fernando")
                .title("Meu Livro")
                .isbn("123456")
                .build();
        String json = new ObjectMapper().writeValueAsString(dto);
        /*
         *    quando formos salvar um Book não será mais um json coforme montamos, mas será um book
         *    para isto implementamos o service a baixo.class
         */
        Book saveBook = Book.builder()
                .id(10L)
                .autor("Fernando")
                .title("Meu Livro")
                .isbn("123456")
                .build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(saveBook);



        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("autor").value(dto.getAutor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));

    }

    @Test
    @DisplayName("Created Invalid Book")
    public void createInvalidBookTest() {

    }


}
