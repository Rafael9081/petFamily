package br.com.petfamily.canilapi.model;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
public class Ninhada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataNascimento;
    private int quantidadeMachos;
    private int quantidadeFemeas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mae_id", nullable = false)
    private Cachorro mae;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pai_id", nullable = false)
    private Cachorro pai;

    public Ninhada(LocalDate dataNascimento, int machos, int femeas, Cachorro mae, Cachorro pai) {
        // Construtor padrão necessário para JPA
    }

    public Ninhada(Long id, LocalDate dataNascimento, int quantidadeMachos, int quantidadeFemeas, Cachorro mae, Cachorro pai) {
        this.id = id;
        this.dataNascimento = dataNascimento;
        this.quantidadeMachos = quantidadeMachos;
        this.quantidadeFemeas = quantidadeFemeas;
        this.mae = mae;
        this.pai = pai;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public int getQuantidadeMachos() {
        return quantidadeMachos;
    }

    public void setQuantidadeMachos(int quantidadeMachos) {
        this.quantidadeMachos = quantidadeMachos;
    }

    public int getQuantidadeFemeas() {
        return quantidadeFemeas;
    }

    public void setQuantidadeFemeas(int quantidadeFemeas) {
        this.quantidadeFemeas = quantidadeFemeas;
    }

    public Cachorro getMae() {
        return mae;
    }

    public void setMae(Cachorro mae) {
        this.mae = mae;
    }

    public Cachorro getPai() {
        return pai;
    }

    public void setPai(Cachorro pai) {
        this.pai = pai;
    }

    public int getQuantidadeTotal() {
        return quantidadeMachos + quantidadeFemeas;
    }
}
