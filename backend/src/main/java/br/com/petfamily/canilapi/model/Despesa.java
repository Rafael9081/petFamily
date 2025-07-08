package br.com.petfamily.canilapi.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Despesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    private double valor;
    @Column(name = "data_despesa")
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private CategoriaDespesa categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cachorro_id", nullable = false)
    private Cachorro cachorro;

    public Despesa() {
        // Construtor padrão necessário para JPA
    }

    public Despesa(String descricao, double valor, LocalDate data, CategoriaDespesa categoria) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Cachorro getCachorro() {
        return cachorro;
    }

    public void setCachorro(Cachorro cachorro) {
        this.cachorro = cachorro;
    }

    public CategoriaDespesa getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDespesa categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] - R$ %.2f (%s)",
                data, categoria, valor, descricao);
    }
}
