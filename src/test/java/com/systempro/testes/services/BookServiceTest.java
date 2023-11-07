package com.systempro.testes.services;

import com.systempro.testes.domain.Book;
import com.systempro.testes.exceptions.BusinessException;
import com.systempro.testes.repositories.BookRepository;
import com.systempro.testes.services.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BookServiceTest {

    BookService service;
    @MockBean
    BookRepository repository;

    /*  esta anotação  @BeforeEach faz com que o metodo setUp seja
     * execultado antes de todos os tests
     */

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        // cenario
        Book book = createdValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Mockito.when(repository.save(book)).thenReturn(
                Book
                        .builder()
                        .id(1L)
                        .autor("Fulano")
                        .title("Livro do sicrano")
                        .isbn("123")
                        .build());
        // excução

        Book savedBook = service.save(book);

        // vedificação

        //assert ta dizendo verifique se id não é nulo
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAutor()).isEqualTo("Fulano");
        assertThat(savedBook.getTitle()).isEqualTo("Livro do sicrano");
        assertThat(savedBook.getIsbn()).isEqualTo("123");
    }


    @Test
    @DisplayName("ISBN Duplicate")
    public void shouldNotSaveAsBookWithDuplicatedISBN() {
        //cenarion
        Book book = createdValidBook();
        /*
        quando meu test verificar  o repository.existsByIsbn, passando qualquer string
        tipo Mockito.anyString() vai retornar verdadeiro ==thenReturn(true);
        */
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //exceução
        Throwable exeptions = Assertions.catchThrowable(() -> service.save(book));

        //verificação
        assertThat(exeptions)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN duplicate");

        //verifique se meu repository nunva (,Mockito.never())  vai execultar o metodo de salvar este book
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    private static Book createdValidBook() {
        return Book
                .builder()
                .autor("Fulano")
                .title("Livro do sicrano")
                .isbn("123")
                .build();
    }
}
