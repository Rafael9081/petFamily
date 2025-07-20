package br.com.petfamily.canilapi.controller.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para a visualização detalhada de uma ninhada, incluindo pais e filhotes.
 */
public record NinhadaDetalhesDTO(
        Long id,
        LocalDate dataNascimento,
        CachorroResumoDTO mae,
        CachorroResumoDTO pai,
        List<CachorroResumoDTO> filhotes
) {
}