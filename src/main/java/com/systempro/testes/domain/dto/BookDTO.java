package com.systempro.testes.domain.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String autor;
    @NotEmpty
    private String isbn;
}
