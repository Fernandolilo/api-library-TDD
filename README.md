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
