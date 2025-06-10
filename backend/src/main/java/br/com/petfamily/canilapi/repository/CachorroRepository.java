package br.com.petfamily.canilapi.repository;

import br.com.petfamily.canilapi.model.Cachorro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CachorroRepository extends JpaRepository<Cachorro, Long> {

}
