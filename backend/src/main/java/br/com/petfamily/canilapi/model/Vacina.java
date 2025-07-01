package br.com.petfamily.canilapi.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Vacina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private LocalDate dataAplicacao;
    private LocalDate dataProximaAplicacao;
    private double valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cachorro_id")
    private Cachorro cachorro;

    public Cachorro getCachorro() {
        return cachorro;
    }

    public void setCachorro(Cachorro cachorro) {
        this.cachorro = cachorro;
    }

    public Vacina() {

    }

    public Vacina(String nome, LocalDate dataAplicacao, LocalDate dataProximaAplicacao, double valor) {
        this.nome = nome;
        this.dataAplicacao = dataAplicacao;
        this.dataProximaAplicacao = dataProximaAplicacao;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataAplicacao() {
        return dataAplicacao;
    }

    public void setDataAplicacao(LocalDate dataAplicacao) {
        this.dataAplicacao = dataAplicacao;
    }

    public LocalDate getDataProximaAplicacao() {
        return dataProximaAplicacao;
    }

    public void setDataProximaAplicacao(LocalDate dataProximaAplicacao) {
        this.dataProximaAplicacao = dataProximaAplicacao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vacina vacina = (Vacina) o;
        return Objects.equals(id, vacina.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
