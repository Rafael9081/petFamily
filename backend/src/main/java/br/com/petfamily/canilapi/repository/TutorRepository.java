package br.com.petfamily.canilapi.repository;

import br.com.petfamily.canilapi.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<Tutor, Long> {


    // Método para encontrar um Tutor pelo e-mail
    Tutor findByEmail(String email);


    // Método para verificar se um Tutor existe pelo e-mail
    boolean existsByEmail(String email);

    // Método para encontrar um Tutor pelo nome
    Tutor findByNome(String nome);
}
