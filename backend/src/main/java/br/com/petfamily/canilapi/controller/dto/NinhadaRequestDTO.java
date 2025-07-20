package br.com.petfamily.canilapi.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.List;

public record NinhadaRequestDTO(
        @NotNull(message = "A data de nascimento é obrigatória.")
        @PastOrPresent(message = "A data de nascimento não pode ser no futuro.")
        LocalDate dataNascimento,

        @NotNull(message = "O ID da mãe é obrigatório.")
        Long maeId,

        @NotNull(message = "O ID do pai é obrigatório.")
        Long paiId,

        // Garante que as validações dentro de FilhoteRequestDTO sejam acionadas.
        @Valid
        List<FilhoteRequestDTO> filhotes
) {
}