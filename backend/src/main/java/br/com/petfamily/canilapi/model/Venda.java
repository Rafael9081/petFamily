package br.com.petfamily.canilapi.model;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double valorVenda;
    private LocalDate dataVenda;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cachorro_id", unique = true, nullable = false)
    private Cachorro cachorroVendido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novo_tutor_id", nullable = false)
    private Tutor novoTutor;

    public Venda(double valorVenda, LocalDate dataVenda) {
        this.valorVenda = valorVenda;
        this.dataVenda = dataVenda;
    }

    public Venda() {
        // Construtor padrão necessário para JPA
    }

    public Venda(double valorVenda, LocalDate dataVenda, Cachorro cachorroVendido, Tutor novoTutor) {
        this.valorVenda = valorVenda;
        this.dataVenda = dataVenda;
        this.cachorroVendido = cachorroVendido;
        this.novoTutor = novoTutor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(double valorVenda) {
        this.valorVenda = valorVenda;
    }

    public LocalDate getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }

    public Cachorro getCachorroVendido() {
        return cachorroVendido;
    }

    public void setCachorroVendido(Cachorro cachorroVendido) {
        this.cachorroVendido = cachorroVendido;
    }

    public Tutor getNovoTutor() {
        return novoTutor;
    }

    public void setNovoTutor(Tutor novoTutor) {
        this.novoTutor = novoTutor;
    }


    @Override
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
        // Usar um valor padrão caso o tutor seja nulo para evitar NullPointerException
        String nomeTutor = (novoTutor != null) ? novoTutor.getNome() : "N/A";
        return String.format("Vendido para %s por R$ %.2f em %s",
                nomeTutor, valorVenda, dataVenda);
    }
}
