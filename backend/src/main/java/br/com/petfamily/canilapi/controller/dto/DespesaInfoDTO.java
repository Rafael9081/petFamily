package br.com.petfamily.canilapi.controller.dto;

import java.time.LocalDate;

// DTO para a lista de despesas do cachorro
public record DespesaInfoDTO(Long id, String descricao, double valor, LocalDate data) {
}