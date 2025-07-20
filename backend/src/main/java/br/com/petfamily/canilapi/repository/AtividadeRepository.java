package br.com.petfamily.canilapi.repository;

import br.com.petfamily.canilapi.repository.projection.AtividadeRecenteProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import br.com.petfamily.canilapi.model.Cachorro;

import java.util.List;

@Repository
public interface AtividadeRepository extends JpaRepository<Cachorro, Long> {
    @Query(
            value = "SELECT * FROM (" +
                    "    (SELECT " +
                    "        'VENDA' AS tipo, " +
                    "        CONCAT('Cachorro ''', c.nome, ''' vendido para ''', t.nome, '''') AS descricao, " +
                    "        v.data AS data, " + // Já é um timestamp, não precisa de cast
                    "        c.id AS entidadeId " +
                    "    FROM venda v " +
                    "    JOIN cachorro c ON v.cachorro_id = c.id " +
                    "    JOIN tutor t ON v.novo_tutor_id = t.id) " +
                    "UNION ALL " +
                    "    (SELECT " +
                    "        'NINHADA' AS tipo, " +
                    "        CONCAT('Ninhada registrada para ''', mae.nome, ''' e ''', pai.nome, '''') AS descricao, " +
                    "        CAST(n.data_nascimento AS TIMESTAMP) AS data, " + // <-- CORREÇÃO AQUI
                    "        mae.id AS entidadeId " +
                    "    FROM ninhada n " +
                    "    JOIN cachorro mae ON n.mae_id = mae.id " +
                    "    JOIN cachorro pai ON n.pai_id = pai.id) " +
                    "UNION ALL " +
                    "    (SELECT " +
                    "        'DESPESA' AS tipo, " +
                    "        CONCAT('Despesa de R$ ', d.valor, ' para ''', c.nome, ''': ', d.descricao) AS descricao, " +
                    "        CAST(d.data_despesa AS TIMESTAMP) AS data, " + // <-- CORREÇÃO AQUI
                    "        c.id AS entidadeId " +
                    "    FROM despesa d " +
                    "    JOIN cachorro c ON d.cachorro_id = c.id)" +
                    ") AS atividades " +
                    "ORDER BY data DESC " +
                    "LIMIT 5",
            nativeQuery = true)
    List<AtividadeRecenteProjection> findAtividadesRecentes();
}

