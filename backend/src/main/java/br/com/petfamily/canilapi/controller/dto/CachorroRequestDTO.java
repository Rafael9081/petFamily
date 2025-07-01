package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.Sexo;
// Imports para as anotações de validação
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record CachorroRequestDTO(
        @NotBlank(message = "O nome não pode ser vazio.")
        String nome,

        @NotNull(message = "O sexo é obrigatório.")
        Sexo sexo,

        @NotBlank(message = "A raça não pode ser vazia.")
        String raca,

        @NotNull(message = "A data de nascimento é obrigatória.")
        @PastOrPresent(message = "A data de nascimento não pode ser no futuro.")
        LocalDate dataNascimento,

        @NotNull(message = "O ID do tutor é obrigatório.")
        Long tutorId
) {
}