package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.Despesa;
import java.time.LocalDate;

// Este DTO representa a forma como uma Despesa será enviada para o cliente.
public record DespesaResponseDTO(
        Long id,
        String descricao,
        Double valor,
        LocalDate data,
        Long cachorroId // Apenas o ID do cachorro, para evitar objetos aninhados complexos
) {
    // Construtor que facilita a conversão da Entidade para o DTO.
    public DespesaResponseDTO(Despesa despesa) {
        this(
                despesa.getId(),
                despesa.getDescricao(),
                despesa.getValor(),
                despesa.getData(),
                despesa.getCachorro().getId() // Extrai apenas o ID do cachorro associado
        );
    }
}