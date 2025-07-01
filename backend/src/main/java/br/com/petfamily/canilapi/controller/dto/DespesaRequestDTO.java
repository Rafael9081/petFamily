package br.com.petfamily.canilapi.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record DespesaRequestDTO(
        @NotBlank(message = "A descrição da despesa não pode ser vazia.")
        String descricao,

        @NotNull(message = "O valor da despesa é obrigatório.")
        @Positive(message = "O valor da despesa deve ser maior que zero.")
        Double valor,
        LocalDate data
) {
}