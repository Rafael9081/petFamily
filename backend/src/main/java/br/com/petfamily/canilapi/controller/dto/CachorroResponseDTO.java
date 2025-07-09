package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Sexo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record CachorroResponseDTO(
        Long id,
        String nome,
        Sexo sexo,
        String raca,
        LocalDate dataNascimento,
        boolean foiVendido,
        TutorInfoDTO tutor,
        List<DespesaInfoDTO> despesas
) {
    // Construtor que faz o mapeamento da Entidade para o DTO
    public CachorroResponseDTO(Cachorro cachorro) {
        this(
                cachorro.getId(),
                cachorro.getNome(),
                cachorro.getSexo(),
                cachorro.getRaca(),
                cachorro.getDataNascimento(),
                cachorro.isFoiVendido(),
                // Mapeia o tutor, se existir
                cachorro.getTutor() != null ? new TutorInfoDTO(cachorro.getTutor().getId(), cachorro.getTutor().getNome()) : null,
                // Mapeia a lista de despesas
                cachorro.getHistoricoDespesas().stream()
                        .map(d -> new DespesaInfoDTO(d.getId(), d.getDescricao(), d.getValor(), d.getData()))
                        .collect(Collectors.toList())
        );
    }
}