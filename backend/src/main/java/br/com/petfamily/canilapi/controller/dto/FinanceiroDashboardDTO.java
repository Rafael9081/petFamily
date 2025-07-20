package br.com.petfamily.canilapi.controller.dto;

import java.math.BigDecimal;

public record FinanceiroDashboardDTO(
        BigDecimal receita,
        BigDecimal despesa,
        BigDecimal lucro
) {
}
