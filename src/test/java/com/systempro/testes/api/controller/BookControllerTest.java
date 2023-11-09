package com.systempro.testes.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systempro.testes.domain.Book;
import com.systempro.testes.domain.dto.BookDTO;
import com.systempro.testes.exceptions.BusinessException;
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

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
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
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors" , hasSize(3)));
    }


    @Test
    @DisplayName("ISBN duplicate")
    public void createdBookWinthDuplicateIsbn()throws Exception{
        BookDTO dto = createNewBook();
        String json = new ObjectMapper().writeValueAsString(dto);
        String messageError = "ISBN duplicate";
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(messageError));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors" , hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(messageError));

    }

    @Test
    @DisplayName("Deve obter dados de um livro por ID")
    public void getBookById() throws Exception {

        //cenario  (given)
        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .autor(createNewBook().getAutor())
                .title(createNewBook().getTitle())
                .isbn(createNewBook().getIsbn())
                .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        //execução (when)

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("autor").value(createNewBook().getAutor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));;
    }


    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir ")
    public void bookNotFoundTest() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNotFound());

    }
    private BookDTO createNewBook() {
        return BookDTO
                .builder()
                .autor("Fernando")
                .title("Meu Livro")
                .isbn("123456")
                .build();
    }
}
