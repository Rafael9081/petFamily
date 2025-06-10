package br.com.petfamily.canilapi.repository;

import br.com.petfamily.canilapi.model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
}
