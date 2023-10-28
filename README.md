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