package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.Despesa;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO que representa a forma como uma Despesa será enviada para o cliente.
 */
public record DespesaResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate data,
        Long cachorroId // Apenas o ID do cachorro, para evitar objetos aninhados complexos
) {
    public DespesaResponseDTO(Despesa despesa) {
        this(
                despesa.getId(),
                despesa.getDescricao(),
                despesa.getValor(),
                despesa.getData(),
                // Verificação de nulidade para evitar NullPointerException
                despesa.getCachorro() != null ? despesa.getCachorro().getId() : null
        );
    }
}