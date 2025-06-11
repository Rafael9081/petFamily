package br.com.petfamily.canilapi.repository;

import br.com.petfamily.canilapi.model.CategoriaDespesa;
import br.com.petfamily.canilapi.model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    // Busca todas as despesas de um cachorro específico
    List<Despesa> findByCachorro_Id(Long cachorroId);

    // Busca todas as despesas dentro de um período
    List<Despesa> findAllByDataBetween(LocalDate dataInicio, LocalDate dataFim);

    // Busca todas as despesas de uma categoria específica
    List<Despesa> findAllByCategoria(CategoriaDespesa categoria);

    // Exemplo de busca combinada: despesas de uma categoria dentro de um período
    List<Despesa> findAllByCategoriaAndDataBetween(CategoriaDespesa categoria, LocalDate dataInicio, LocalDate dataFim);

    // Exemplo de query customizada para somar valores. Retorna um Double.
    @Query("SELECT SUM(d.valor) FROM Despesa d WHERE d.data BETWEEN :inicio AND :fim")
    Double sumDespesasByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}