package br.com.petfamily.canilapi.repository;

import br.com.petfamily.canilapi.model.Cachorro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CachorroRepository extends JpaRepository<Cachorro, Long> {
    @Query("SELECT c FROM Cachorro c " +
            "LEFT JOIN FETCH c.tutor " +
            "LEFT JOIN FETCH c.historicoDespesas " +
            "LEFT JOIN FETCH c.registroVenda " +
            "WHERE c.id = :id")
    Optional<Cachorro> findByIdWithAssociations(@Param("id") Long id);

    @Query(value = "SELECT DISTINCT c FROM Cachorro c LEFT JOIN FETCH c.tutor",
            countQuery = "SELECT COUNT(c) FROM Cachorro c")
    Page<Cachorro> findAllWithTutor(Pageable pageable);

    @Query("SELECT DISTINCT c FROM Cachorro c LEFT JOIN FETCH c.tutor LEFT JOIN FETCH c.historicoDespesas")
    List<Cachorro> findAllWithAssociations();

}
