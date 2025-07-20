package br.com.petfamily.canilapi.repository;

import br.com.petfamily.canilapi.model.Venda;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    /**
     * CORREÇÃO: O nome do método foi alterado de 'findAllByDataBetween' para 'findAllByDataVendaBetween'
     * para corresponder exatamente à propriedade 'dataVenda' na entidade Venda.
     */
    List<Venda> findAllByDataVendaBetween(LocalDate dataInicio, LocalDate dataFim);

    @Query("SELECT v FROM Venda v WHERE v.dataVenda >= :dataInicio")
    List<Venda> findVendasAfter(@Param("dataInicio") LocalDate dataInicio);

    @Query("SELECT v FROM Venda v JOIN FETCH v.cachorro JOIN FETCH v.novoTutor ORDER BY v.dataVenda DESC")
    List<Venda> findTop5RecentSales(Pageable pageable);

    @Query("SELECT SUM(v.valor) FROM Venda v WHERE v.dataVenda BETWEEN :inicio AND :fim")
    BigDecimal sumVendasByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}