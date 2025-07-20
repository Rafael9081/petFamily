package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para receber os dados de um novo filhote ao criar uma ninhada.
 * Utiliza 'record' para concisão e imutabilidade.
 */
public record FilhoteRequestDTO(
        @NotBlank(message = "O nome do filhote é obrigatório.")
        String nome,

        @NotNull(message = "O sexo do filhote é obrigatório.")
        Sexo sexo
) {
}