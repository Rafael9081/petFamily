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
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nomes dos campos simplificados para maior clareza
    private double valor;
    private LocalDate data;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cachorro_id", unique = true, nullable = false)
    private Cachorro cachorro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novo_tutor_id", nullable = false)
    private Tutor novoTutor;

    // Construtor padregido, necessário para JPA, mas desencoraja o uso direto
    protected Venda() {
    }

    // Único construtor público, garantindo que uma Venda sempre seja criada em um estado válido
    public Venda(double valor, LocalDate data, Cachorro cachorro, Tutor novoTutor) {
        this.valor = valor;
        this.data = data;
        this.cachorro = cachorro;
        this.novoTutor = novoTutor;
    }

    // --- Getters e Setters com nomes canônicos ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Tutor getNovoTutor() {
        return novoTutor;
    }

    public void setNovoTutor(Tutor novoTutor) {
        this.novoTutor = novoTutor;
    }

    // --- MOverride
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venda venda = (Venda) o;
        return Objects.equals(id, venda.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String nomeTutor = (novoTutor != null) ? novoTutor.getNome() : "N/A";
        return String.format("Vendido para %s por R$ %.2f em %s",
                nomeTutor, valor, data);
    }
}