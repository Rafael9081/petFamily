package br.com.petfamily.canilapi.repository.projection;

import java.time.LocalDateTime;

public interface AtividadeRecenteProjection {
    String getTipo();
    String getDescricao();
    LocalDateTime getData();
    Long getEntidadeId();
}
