package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.Despesa; // Importe a entidade

import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaInfoDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate data) {

    public DespesaInfoDTO(Despesa despesa) {
        this(despesa.getId(), despesa.getDescricao(), despesa.getValor(), despesa.getData());
    }
}