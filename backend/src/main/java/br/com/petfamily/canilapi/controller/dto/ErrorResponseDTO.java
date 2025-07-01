package br.com.petfamily.canilapi.controller.dto;

import java.time.Instant;

// Usar 'record' gera automaticamente o construtor, getters, equals, hashCode e toString.
public record ErrorResponseDTO(String mensagem, int status, Instant timestamp) {
}