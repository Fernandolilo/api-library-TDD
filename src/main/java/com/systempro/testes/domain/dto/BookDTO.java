package com.systempro.testes.domain.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;
    private String title;
    private String autor;
    private String isbn;
}
