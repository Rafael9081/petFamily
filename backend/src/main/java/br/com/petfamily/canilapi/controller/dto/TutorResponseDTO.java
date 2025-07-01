package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.Tutor;

import java.util.List;
import java.util.stream.Collectors;

public record TutorResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        List<CachorroInfoDTO> cachorros
) {
    // Um construtor que sabe como mapear de uma entidade Tutor para este DTO
    public TutorResponseDTO(Tutor tutor) {
        this(
                tutor.getId(),
                tutor.getNome(),
                tutor.getEmail(),
                tutor.getTelefone(),
                // Mapeia a lista de entidades Cachorro para uma lista de CachorroInfoDTOs
                tutor.getCachorros().stream()
                        .map(cachorro -> new CachorroInfoDTO(cachorro.getId(), cachorro.getNome(), cachorro.getRaca()))
                        .collect(Collectors.toList())
        );
    }
}