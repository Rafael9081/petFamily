package br.com.petfamily.canilapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TutorRequestDTO(
        @NotBlank(message = "O nome não pode estar em branco.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @NotBlank(message = "O email não pode estar em branco.")
        @Email(message = "O formato do email é inválido.")
        String email,

        @NotBlank(message = "O telefone não pode estar em branco.")
        @Pattern(regexp = "^\\d{10,11}$", message = "O telefone deve conter apenas números, com 10 ou 11 dígitos (incluindo DDD).")
        String telefone
) {
}