package br.com.petfamily.canilapi.controller.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public record PaginatedResponseDTO<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public PaginatedResponseDTO(Page<T> page) {
        this(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}