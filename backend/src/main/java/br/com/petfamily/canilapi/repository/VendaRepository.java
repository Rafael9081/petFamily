package br.com.petfamily.canilapi.repository;

import br.com.petfamily.canilapi.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findAllByDataBetween(LocalDate dataInicio, LocalDate dataFim);
}
