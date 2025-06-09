package br.com.petfamily.canilapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Vacina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private LocalDate dataAplicacao;
    private LocalDate dataProximaAplicacao;
    private double valor;

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
}
