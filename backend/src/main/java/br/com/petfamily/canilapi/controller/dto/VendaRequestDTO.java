package br.com.petfamily.canilapi.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record VendaRequestDTO(
        @NotNull(message = "O ID do novo tutor é obrigatório.")
        Long novoTutorId,

        @NotNull(message = "O valor da venda é obrigatório.")
        @Positive(message = "O valor da venda deve ser positivo.")
        Double valor,

        @NotNull(message = "A data da venda é obrigatória.")
        @PastOrPresent(message = "A data da venda não pode ser no futuro.") // Validação extra útil.
        LocalDate dataVenda
) {
}