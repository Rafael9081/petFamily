package br.com.petfamily.canilapi.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vendas")
@Getter
@Setter
@ToString(exclude = {"novoTutor", "cachorro"})
@EqualsAndHashCode(of = "id")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataVenda;
    private BigDecimal valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novo_tutor_id", nullable = false)
    private Tutor novoTutor;

    @OneToOne
    @JoinColumn(name = "cachorro_id", unique = true, nullable = false)
    private Cachorro cachorro;

    public Venda() {
    }

    public Venda(BigDecimal valor, LocalDate dataVenda, Cachorro cachorro, Tutor novoTutor) {
        this.valor = valor;
        this.dataVenda = dataVenda;
        this.cachorro = cachorro;
        this.novoTutor = novoTutor;
    }

}