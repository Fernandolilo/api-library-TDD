# api-library-TDD

iniciando curso de TDD

1 criando um test passando uma rota, test vai falhar pois não existe a rota.

2 criado a rota BookController, por hora retornando null,
e criado o BookDTO apenas para testes,

temos agora um test ok.

3 passando dados staticos para teste e test ok;
```lombok.config
    
BookDTO dto = new BookDTO();
dto.setAutor("Autor");
dto.setTitle("Meu Livro");
dto.setIsbn("123456");
dto.setId(1l);
```
criamos agora um JSON para não passar mais o json null

```lombok.config
BookDTO dto = BookDTO
.builder()
.id(1L)
.autor("Fernando")
.title("Meu Livro")
.isbn("123456")
.build();

String json = new ObjectMapper().writeValueAsString(dto);
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
```
* quando formos salvar um Book não será mais um json coforme montamos, mas será um book
* para isto implementamos o service a baixo.class
*/
 
```lombok.config
  Book saveBook = Book.builder()
  .id(10L)
  .autor("Fernando")
  .title("Meu Livro")
  .isbn("123456")
  .build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(saveBook);

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
```
/*

* quando formos salvar um Book não será mais um json coforme montamos, mas será um book
* para isto implementamos o service a baixo.class
*/
```lombok.config
  Book saveBook = Book.builder()
  .id(10L)
  .autor("Fernando")
  .title("Meu Livro")
  .isbn("123456")
  .build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(saveBook);
```
refatoramo nosso test, agora passando um DTO para salvar uma entidade, e retornado um DTO.
nosso test ficou desta forma retornando sucesso!

```lombok.config
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
```
/*
* quando formos salvar um Book não será mais um json coforme montamos, mas será um book
* para isto implementamos o service a baixo.class
*/
  
```lombok.config
  Book saveBook = Book.builder()
  .id(10L)
  .autor("Fernando")
  .title("Meu Livro")
  .isbn("123456")
  .build();

  BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(saveBook);
```

```java
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
```
add o Model mapper ao no cod com intuito de diminuir o cod, tornando o mais clean., 
```xml
 <!-- https://mvnrepository.com/artifact/org.modelmapper/modelmapper -->
        <dependency>
            <groupId>org.modelmapper</groupId>
            <artifactId>modelmapper</artifactId>
            <version>3.2.0</version>
        </dependency>
```

nosso controller agora esta mais limpo

```java
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
    public BookDTO create(@RequestBody BookDTO dto) {
        Book entity =
               modelMapper.map(dto, Book.class);
        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }
}

```
Criamos agora nosso service para de fato salvar no banco de dados


```java
@Service
public class BookServiceImpl implements BookService {
private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }
    @Override
    public Book save(Book book) {
        return repository.save(book);
    }
}

```
nosso service test para testar a implementação via mockito, salvando um book;

```java
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
        Book book =
                Book
                        .builder()
                        .autor("Fulano")
                        .title("Livro do sicrano")
                        .isbn("123")
                        .build();

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
}
```

agora vamos testar a seguinte coisa, caso alguem tente salvar um novo livro,
caso passe algum dado faltando nos trara uma informacao de error

```java
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

```

dentro de nosso controller foi feito este handler com proposito de verificar apenas se o corpo esta vindo completo

```java
 @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidExceptions(MethodArgumentNotValidException exception){
       BindingResult bindingResult = exception.getBindingResult();
        return new ApiErrors(bindingResult);
    }
```

test de regra de negocio, caso haja algum dado em duplicidade retorna  um erro para nós
```java
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
```

passando para o service a responsabilidade de veificar com paramentros no metodo se ISBN ja existe;
caso exista retorna uma exceção

```java
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
```

nete test vamos validar um livro existente, findByExists com RepositoryTest

```java
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
        /*passando um ISBN mock*/
        String isbn = "123";
        
        /*Intanciando um Book no BD*/
        Book book = Book.builder()
                .autor("Fulano")
                .title("Livro do sicrano")
                .isbn(isbn)
                .build();
        
        /*Chamando um entityManager que é do JPA para persistir o Book no BD */
        entityManager.persist(book);

        //execução
 
        /* 
        fazendo uma veirificação se o ISBN existe, 
        caso sim retorna true na verificação a baixo
        */ 
        boolean exixts = bookRepository.existsByIsbn(isbn);
        //verificação

        assertThat(exixts).isTrue();
    }
}
```

para implementar um findById 

01 -> criamos este test

```java
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

```


no nosso service criamos o nosso getById

```java
 Optional<Book> getById(Long id);
```
agora no nosso service.Impl refletimos nosso cod

```java
  @Override
    public Optional<Book> getById(Long id) {
        return Optional.empty();
    }
```

veja a sequencia primeiro no ControlerTest, apos service, por fim no  service.Impl

no controller getById test ok

```java
  @GetMapping(value = "/{id}")
    public BookDTO getById(@PathVariable Long id) {
        Book book = service.getById(id).get();
        return modelMapper.map(book, BookDTO.class);
    }

```

implementado agora  NotFoundException

```java
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
```

nosso endpoint estava sem tratamento de erros

```java
 @GetMapping(value = "/{id}")
    public BookDTO getById(@PathVariable Long id) {

        return service.getById(id).map(book -> )
        Book book = service.getById(id).get( modelMapper.map(book, BookDTO.class) );
        return
    }
```

efetuando uma auteração no nosso findById para que este test fosse execultado com sucesso!

```java
 @GetMapping(value = "/{id}")
    public BookDTO getById(@PathVariable Long id) {

        return service
                .getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
```

crimanos dois tests para analisar a deleção, um com retorno de 204 No_Content, outro com um 400 Not Found
por que implementado ambos, caso seja deletado precisamos de um retorno de alguma informação da API,para isto temos o retorno do rest == 204
caso haja algum erro teremos um retorna da API com uma exceção de Not Found quando nada foi achado.
```java
  @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTests() throws Exception{

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform( request )
                .andExpect( status().isNoContent() );
    }

    @Test
    @DisplayName("Deve retornar resourcer not found quando não encontrar um livro para deletar")
    public void deleteInexistentBookTests() throws Exception{

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform( request )
                .andExpect( status().isNotFound() );
    }

```

tests de update de livros,
como acima ja descrito, são dois tests um para update ok e outro para not found

```java
@Test
    @DisplayName("Deve  atualizar um livro  ")
    public void updateBook() throws Exception {

        Long id = 1l;
        // cria o json
        String json = new ObjectMapper().writeValueAsString(createNewBook());

        
        //pega o objeto completo
        Book updateBook = Book.builder()
                .id(id)
                .autor("some autor")
                .title("some livro")
                .isbn("456789")
                .build();


        //faz a atualização
        Book updetedBook = Book.builder()
                .id(id)
                .autor("Fernando")
                .title("Meu Livro")
                .isbn("123456")
                .build();

        BDDMockito.given(service.getById(id))
                .willReturn(Optional.of(updateBook));

        BDDMockito.given(service.update(updateBook)).willReturn(updetedBook);


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("autor").value(createNewBook().getAutor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

    }
```
    
test para veirificar uma busca por ID de um livro 
        
```java
@DisplayName("Deve retornar um livro por id")
    @Test
    public void getLivroByIdTest(){
        Long id = 1L;
        // estancia um book
        Book book= createdValidBook();

        book.setId(id);

        //verifica se há um book
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        // execução de test
        Optional<Book> findByBook = service.getById(id);

        //verificação
        assertThat(findByBook.isPresent()).isFalse();

    }


@Override
public Optional<Book> getById(Long id) {
    return repository.findById(id);
}
```

test para livro inexistente

```java
 @DisplayName("Not found by id livro")
    @Test
    public void getNotFoundLivroIdTest(){
        Long id = 1L;
        // estancia um book
        Book book= createdValidBook();

        book.setId(id);

        //verifica se há um book
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        // execução de test
        Optional<Book> findByBook = service.getById(id);

        //verificação
        assertThat(findByBook.isPresent()).isTrue();
        assertThat( findByBook.get() .getId() ).isEqualTo(id);
        assertThat( findByBook.get() .getAutor() ).isEqualTo(book.getAutor());
        assertThat( findByBook.get() .getTitle() ).isEqualTo(book.getTitle());
        assertThat( findByBook.get() .getIsbn() ).isEqualTo(book.getIsbn());
    }
```

Teste de integração...

findbyId para um livro 

```java
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
```

test de deleção 
 ```java
@Test
    @DisplayName("Deve deletar um livro")
    public void deleteBooktest(){

        Book book = Book.builder().id(1L).build();

        //execução
       org.junit.jupiter.api.Assertions.assertDoesNotThrow(  () ->
               service.delete(book));

        //verificação

        Mockito
                .verify( repository, Mockito.times( 1))
                .delete(book);

}
      

```

falha ao tentar deletar livro inexistente 

```java
  @Test
        @DisplayName("Deve lancar um error ao tentar deletar um livro")
        public void deleteInvalidBookTest(){
            Book book = new Book();

            org.junit.jupiter.api.Assertions.assertThrows(
                    IllegalArgumentException.class,() ->
                    service.delete(book));

            Mockito
                    .verify( repository, Mockito.never() )
                    .delete(book);
        }
```

teste de integração no repository metodo salvar

```java
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

    
```
teste de integração com repository metodo deletar

```java
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
```