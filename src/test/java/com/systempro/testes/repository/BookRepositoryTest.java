package com.systempro.testes.repository;

import com.systempro.testes.domain.Book;
import com.systempro.testes.repositories.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existit um livro na base com isbn  informado")
    public void returnTrueWhenIsbnExisits() {
        //cenario
        String isbn = "123";
        Book book = createNewBook(isbn);
        entityManager.persist(book);

        //execução

        boolean exixts = bookRepository.existsByIsbn(isbn);
        //verificação

        assertThat(exixts).isTrue();
    }

    private static Book createNewBook(String isbn) {
        return Book.builder()
                .autor("Fulano")
                .title("Livro do sicrano")
                .isbn(isbn)
                .build();
    }

    //test de integração
    @Test
    @DisplayName("Deve obter um livro por id")
    public void findByIdTest(){

        //cenario
        Book book = createNewBook("123");
        entityManager.persist(book);

        //execução
      Optional<Book> foundBook = bookRepository.findById(book.getId());

      //verificação
        assertThat(foundBook.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Save boob")
    public void saveBookTest(){

        //cenario
        Book book = createNewBook("123");

        //execução
        Book savedBook = bookRepository.save(book);

        /*
        * caso haja um book de fato salvo tera que me retornar o ID do memso
        */

        //verificação
        assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        //cenario
        Book book = createNewBook("123");
        entityManager.persist(book);
        //veifico se na base há um book
        Book foundBook = entityManager.find(Book.class, book.getId());

        //execução
         bookRepository.delete(foundBook);
         Book deletedBook = entityManager.find(Book.class, book.getId());
         assertThat(deletedBook).isNull();
    }

}
