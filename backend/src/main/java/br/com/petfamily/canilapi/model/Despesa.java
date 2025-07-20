package br.com.petfamily.canilapi.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "despesas")
@Getter
@Setter
@ToString(exclude = "cachorro")
@EqualsAndHashCode(of = "id")
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    private BigDecimal valor;
    private LocalDate data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cachorro_id", nullable = false)
    private Cachorro cachorro;

    public Despesa() {
    }

    // ADICIONE ESTE CONSTRUTOR DE CONVENIÊNCIA
    public Despesa(String descricao, BigDecimal valor, LocalDate data) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
    }
}