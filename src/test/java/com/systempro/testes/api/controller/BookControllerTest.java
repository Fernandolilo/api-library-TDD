package com.systempro.testes.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systempro.testes.domain.dto.BookDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

    @Test
    @DisplayName("Created Book")
    public void createBookTest() throws Exception{
        BookDTO dto = BookDTO
                .builder()
                .id(1L)
                .autor("Fernando")
                .title("Meu Livro")
                .isbn("123456")
                .build();
        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated() )
                .andExpect( jsonPath( "id").isNotEmpty() )
                .andExpect( jsonPath( "title").value(dto.getTitle()) )
                .andExpect( jsonPath( "autor").value(dto.getAutor()) )
                .andExpect( jsonPath( "isbn").value(dto.getIsbn()) );

    }

    @Test
    @DisplayName("Created Invalid Book")
    public void createInvalidBookTest() {

    }


}
