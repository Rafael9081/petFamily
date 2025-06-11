package br.com.petfamily.canilapi.repository;

import br.com.petfamily.canilapi.model.Ninhada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NinhadaRepository extends JpaRepository<Ninhada, Long> {
    List<Ninhada> findByMae_Id(Long maeId);

    @Query("SELECT n FROM Ninhada n WHERE YEAR(n.dataNascimento) = :ano")
    List<Ninhada> findByAnoNascimento(@Param("ano") int ano);
}
