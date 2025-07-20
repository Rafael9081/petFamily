package br.com.petfamily.canilapi.repository;

import br.com.petfamily.canilapi.model.Ninhada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NinhadaRepository extends JpaRepository<Ninhada, Long> {

    /**
     * CORREÇÃO: Busca uma página de ninhadas, trazendo (JOIN FETCH) os dados
     * da mãe e do pai na mesma consulta para evitar LazyInitializationException.
     */
    @Override
    @Query(value = "SELECT n FROM Ninhada n JOIN FETCH n.mae JOIN FETCH n.pai",
            countQuery = "SELECT COUNT(n) FROM Ninhada n")
    Page<Ninhada> findAll(Pageable pageable);

    /**
     * CORREÇÃO: Busca uma ninhada por ID, trazendo também os dados completos
     * da mãe, do pai e dos filhotes na mesma consulta.
     * @param id O ID da ninhada.
     * @return Um Optional contendo a ninhada com todas as suas associações carregadas.
     */
    @Query("SELECT n FROM Ninhada n " +
            "JOIN FETCH n.mae " +
            "JOIN FETCH n.pai " +
            "LEFT JOIN FETCH n.filhotes " + // LEFT JOIN para caso não hajam filhotes
            "WHERE n.id = :id")
    Optional<Ninhada> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT n FROM Ninhada n JOIN FETCH n.pai WHERE n.mae.id = :maeId")
    List<Ninhada> findByMaeIdWithAssociations(@Param("maeId") Long maeId);

    @Query("SELECT n FROM Ninhada n JOIN FETCH n.mae JOIN FETCH n.pai WHERE FUNCTION('YEAR', n.dataNascimento) = :ano")
    List<Ninhada> findByAnoNascimento(@Param("ano") int ano);


}