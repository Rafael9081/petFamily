package br.com.petfamily.canilapi.repository;

import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.StatusCachorro; // <-- ESTA É A LINHA QUE FALTAVA
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CachorroRepository extends JpaRepository<Cachorro, Long> {
    // ... seus outros métodos @Query
    @Query("SELECT c FROM Cachorro c " +
            "LEFT JOIN FETCH c.tutor " +
            "LEFT JOIN FETCH c.historicoDespesas " +
            "LEFT JOIN FETCH c.registroVenda " +
            "WHERE c.id = :id")
    Optional<Cachorro> findByIdWithAssociations(@Param("id") Long id);

    @Query(value = "SELECT DISTINCT c FROM Cachorro c " +
            "LEFT JOIN FETCH c.tutor " +
            "LEFT JOIN FETCH c.registroVenda " +
            "LEFT JOIN FETCH c.historicoDespesas",
            countQuery = "SELECT COUNT(DISTINCT c) FROM Cachorro c")
    Page<Cachorro> findAllForListView(Pageable pageable);

    @Query("SELECT DISTINCT c FROM Cachorro c LEFT JOIN FETCH c.tutor LEFT JOIN FETCH c.historicoDespesas")
    List<Cachorro> findAllWithAssociations();

    // Agora este método é válido
    long countByStatus(StatusCachorro status);

    @Query("SELECT c FROM Cachorro c LEFT JOIN FETCH c.tutor") // Exemplo de busca com uma relação ToOne
    Page<Cachorro> findAllWithTutor(Pageable pageable);

    @Query(value = "SELECT c FROM Cachorro c",
            countQuery = "SELECT count(c) FROM Cachorro c")
    Page<Cachorro> findByPage(Pageable pageable);

    // CORREÇÃO: Adicionar o "LEFT JOIN FETCH c.tutor"
    @Query("SELECT DISTINCT c FROM Cachorro c " +
            "LEFT JOIN FETCH c.tutor " + // <-- Adicione esta linha!
            "LEFT JOIN FETCH c.historicoDespesas " +
            "LEFT JOIN FETCH c.registroVenda rv " +
            "LEFT JOIN FETCH rv.novoTutor " +
            "WHERE c.id IN :ids")
    List<Cachorro> findAllWithCollectionsByIds(@Param("ids") List<Long> ids);
}