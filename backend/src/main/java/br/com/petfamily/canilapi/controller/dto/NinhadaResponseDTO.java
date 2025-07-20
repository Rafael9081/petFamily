package br.com.petfamily.canilapi.controller.dto;

import br.com.petfamily.canilapi.model.Ninhada;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record NinhadaResponseDTO(
        Long id,
        LocalDate dataNascimento,
        CachorroResumoDTO mae,
        CachorroResumoDTO pai,
        int totalFilhotes,
        List<CachorroResumoDTO> filhotes
) {
    public NinhadaResponseDTO(Ninhada ninhada) {
        this(
                ninhada.getId(),
                ninhada.getDataNascimento(),
                // Adiciona verificação para evitar NullPointerException se a mãe ou o pai não existirem
                ninhada.getMae() != null ? new CachorroResumoDTO(ninhada.getMae()) : null,
                ninhada.getPai() != null ? new CachorroResumoDTO(ninhada.getPai()) : null,
                // A lógica para filhotes jstá segura e correta
                ninhada.getFilhotes() != null ? ninhada.getFilhotes().size() : 0,
                ninhada.getFilhotes() != null ?
                        ninhada.getFilhotes().stream()
                                .map(CachorroResumoDTO::new)
                                .collect(Collectors.toList())
                        : Collections.emptyList()
        );
    }
}