package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.Venda;
import java.time.LocalDate;

public record VendaResponseDTO(
        Long id,
        Double valor,
        LocalDate data,
        Long cachorroId,
        String cachorroNome,
        Long tutorId,
        String tutorNome
) {
    // Este construtor agora funciona perfeitamente com a entidade Venda refatorada.
    public VendaResponseDTO(Venda venda) {
        this(
                venda.getId(),
                venda.getValor(),
                venda.getData(),
                venda.getCachorro().getId(),
                venda.getCachorro().getNome(),
                venda.getNovoTutor().getId(),
                venda.getNovoTutor().getNome()
        );
    }
}