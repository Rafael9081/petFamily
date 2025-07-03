package br.com.petfamily.canilapi.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VendaRequestDTO(
        @NotNull(message = "O ID do cachorro é obrigatório.")
        Long cachorroId,

        @NotNull(message = "O ID do novo tutor é obrigatório.")
        Long novoTutorId,

        @NotNull(message = "O valor da venda é obrigatório.")
        @Positive(message = "O valor da venda deve ser positivo.")
        Double valor
) {
}