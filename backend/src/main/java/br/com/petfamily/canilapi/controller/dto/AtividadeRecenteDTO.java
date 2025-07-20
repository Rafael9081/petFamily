package br.com.petfamily.canilapi.controller.dto;

import java.time.LocalDateTime;
public record AtividadeRecenteDTO(
        String tipo,
        String descricao,
        LocalDateTime data,
        Long entidadeId
) {
}
