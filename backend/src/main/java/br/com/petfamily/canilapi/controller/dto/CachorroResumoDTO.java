package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Sexo;

/**
 * DTO para representar um resumo de um cachorro, usado em listas e detalhes.
 */
public record CachorroResumoDTO(
        Long id,
        String nome,
        Sexo sexo
) {
    // Construtor de conveniência para facilitar o mapeamento a partir da entidade
    public CachorroResumoDTO(Cachorro cachorro) {
        this(cachorro.getId(), cachorro.getNome(), cachorro.getSexo());
    }
}