package com.systempro.testes.repositories;
import com.systempro.testes.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface BookRepository extends  JpaRepository<Book, Long>{

    //query method
    boolean existsByIsbn(String isbn);
}
