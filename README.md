# api-library-TDD

iniciando curso de TDD

1 criando um test passando uma rota, test vai falhar pois não existe a rota.

2 criado a rota BookController, por hora retornando null,
e criado o BookDTO apenas para testes, 

temos agora um test ok.

3 passando dados staticos para teste e test ok;

BookDTO dto = new BookDTO();
dto.setAutor("Autor");
dto.setTitle("Meu Livro");
dto.setIsbn("123456");
dto.setId(1l);


criamos agora um JSON para não passar mais o json null

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
refatoramo nosso test, agora passando um DTO para salvar uma entidade, e retornado um DTO.
nosso test ficou desta forma retornando sucesso!
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


nosso controller;

@RestController
@RequestMapping(value = "/books")
public class BookController {
private final BookService service;

    public BookController(BookService service) s{
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
