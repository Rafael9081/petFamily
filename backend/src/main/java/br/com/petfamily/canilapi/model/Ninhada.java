package br.com.petfamily.canilapi.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
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

    /**
     * Construtor padrão necessário para JPA.
     */
    public Ninhada() {
    }

    /**
     * Construtor para criar uma nova ninhada.
     */
    public Ninhada(LocalDate dataNascimento, int quantidadeMachos, int quantidadeFemeas, Cachorro mae, Cachorro pai) {
        // Validações básicas para garantir a integridade dos dados
        if (dataNascimento == null || mae == null || pai == null) {
            throw new IllegalArgumentException("Data de nascimento, mãe e pai não podem ser nulos.");
        }
        if (quantidadeMachos < 0 || quantidadeFemeas < 0) {
            throw new IllegalArgumentException("A quantidade de filhotes não pode ser negativa.");
        }
        this.dataNascimento = dataNascimento;
        this.quantidadeMachos = quantidadeMachos;
        this.quantidadeFemeas = quantidadeFemeas;
        this.mae = mae;
        this.pai = pai;
    }

    // --- Getters e Setters ---

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

    /**
     * Retorna a quantidade total de filhotes na ninhada.
     * Este é um valor calculado e não é persistido no banco de dados.
     * A anotação @Transient informa ao JPA para ignorar este método.
     */
    @Transient
    public int getQuantidadeTotalFilhotes() {
        return this.quantidadeMachos + this.quantidadeFemeas;
    }

    // --- equals, hashCode e toString ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ninhada ninhada = (Ninhada) o;
        return Objects.equals(id, ninhada.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ninhada{" +
                "id=" + id +
                ", dataNascimento=" + dataNascimento +
                ", totalFilhotes=" + getQuantidadeTotalFilhotes() +
                '}';
    }
}