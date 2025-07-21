package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.StatusCachorro;
import jakarta.validation.constraints.NotNull;

// This record provides a strongly-typed, validated way to receive status updates.
public record CachorroStatusUpdateDTO(
        @NotNull(message = "O novo status não pode ser nulo.")
        StatusCachorro status
) {}