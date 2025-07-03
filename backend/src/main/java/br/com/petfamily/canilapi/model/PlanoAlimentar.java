package br.com.petfamily.canilapi.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class PlanoAlimentar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoRacao;
    private double quantidadeGramasPorDia;
    private int frequenciaDiaria;
    private String instrucoesEspeciais;

    public PlanoAlimentar() {

    }

    public PlanoAlimentar(String tipoRacao, double quantidadeGramasPorDia, int frequenciaDiaria, String instrucoesEspeciais) {
        this.tipoRacao = tipoRacao;
        this.quantidadeGramasPorDia = quantidadeGramasPorDia;
        this.frequenciaDiaria = frequenciaDiaria;
        this.instrucoesEspeciais = instrucoesEspeciais;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoRacao() {
        return tipoRacao;
    }

    public void setTipoRacao(String tipoRacao) {
        this.tipoRacao = tipoRacao;
    }

    public double getQuantidadeGramasPorDia() {
        return quantidadeGramasPorDia;
    }

    public void setQuantidadeGramasPorDia(double quantidadeGramasPorDia) {
        this.quantidadeGramasPorDia = quantidadeGramasPorDia;
    }

    public int getFrequenciaDiaria() {
        return frequenciaDiaria;
    }

    public void setFrequenciaDiaria(int frequenciaDiaria) {
        this.frequenciaDiaria = frequenciaDiaria;
    }

    public String getInstrucoesEspeciais() {
        return instrucoesEspeciais;
    }

    public void setInstrucoesEspeciais(String instrucoesEspeciais) {
        this.instrucoesEspeciais = instrucoesEspeciais;
    }
}
