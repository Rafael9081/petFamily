package br.com.petfamily.canilapi.controller.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para representar um relatório financeiro detalhado para um único cachorro.
 * Utiliza 'record' para imutabilidade e concisão, e BigDecimal para precisão monetária.
 */
public record RelatorioFinanceiroDTO(
        Long cachorroId,
        String nomeCachorro,
        List<DespesaInfoDTO> historicoDespesas,
        BigDecimal custoTotal, // Alterado para BigDecimal
        boolean foiVendido,
        VendaResponseDTO registroVenda,
        BigDecimal lucro // Alterado para BigDecimal, que também pode ser nulo
) {
    // O record gera automaticamente o construtor, getters, equals, hashCode e toString.
    // Nenhum código adicional é necessário.
}