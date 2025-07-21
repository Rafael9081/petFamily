package br.com.petfamily.canilapi.repository;

import br.com.petfamily.canilapi.model.Tutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    // Método para encontrar um Tutor pelo e-mail
    Tutor findByEmail(String email);

    // Método para verificar se um Tutor existe pelo e-mail
    boolean existsByEmail(String email);

    // Método para encontrar um Tutor pelo nome
    Tutor findByNome(String nome);

    // CORREÇÃO: A query agora também ordena os resultados pelo nome do tutor.
    @Query("SELECT t FROM Tutor t LEFT JOIN FETCH t.cachorros ORDER BY t.nome ASC")
    List<Tutor> findAllWithCachorrosSorted();

    @Override
    @EntityGraph(value = "Tutor.withCachorros")
    Page<Tutor> findAll(Pageable pageable);
}