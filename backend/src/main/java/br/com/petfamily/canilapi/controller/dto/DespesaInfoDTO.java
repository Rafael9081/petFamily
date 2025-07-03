package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.Despesa; // Importe a entidade
import java.time.LocalDate;

public record DespesaInfoDTO(Long id, String descricao, double valor, LocalDate data) {

    // --- ADICIONE ESTE CONSTRUTOR ---
    // Ele facilita a conversão da entidade para este DTO.
    public DespesaInfoDTO(Despesa despesa) {
        this(despesa.getId(), despesa.getDescricao(), despesa.getValor(), despesa.getData());
    }
}