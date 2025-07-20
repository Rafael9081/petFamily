package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

// DTO CORRIGIDO
public record CachorroPostRequestDTO(
        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @NotNull(message = "A data de nascimento é obrigatória.")
        @PastOrPresent(message = "A data de nascimento não pode ser no futuro.")
        LocalDate dataNascimento, // Nome e tipo corretos

        @NotBlank(message = "A raça é obrigatória.")
        String raca, // Nome e tipo corretos

        @NotNull(message = "O sexo é obrigatório.")
        Sexo sexo
) {
}