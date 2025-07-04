package br.com.petfamily.canilapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Using a Java Record for a simple, immutable DTO
public record TutorRequestDTO(
        @NotBlank(message = "O nome no pode estar em branco")
        String nome,

        @NotBlank(message = "O email não pode estar em branco")
        @Email(message = "O formato do email é inválido")
        String email
) {}