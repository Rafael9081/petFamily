package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.controller.dto.TutorRequestDTO;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

// VendaRequestFlexDTO.java (um novo DTO)
public record VendaRequestFlexDTO(
        // Continua igual
        @NotNull @Positive BigDecimal valor,
        @NotNull @PastOrPresent LocalDate data,

        // Opcional: ID de um tutor existente
        Long tutorId,

        // Opcional: Dados para criar um novo tutor
        TutorRequestDTO novoTutor
) {
    // Validação para garantir que apenas um dos dois foi enviado
    @AssertTrue(message = "Forneça um tutorId existente ou os dados de um novoTutor, mas não ambos.")
    public boolean isTutorDataValid() {
        return (tutorId != null && novoTutor == null) || (tutorId == null && novoTutor != null);
    }
}