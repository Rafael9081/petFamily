package br.com.petfamily.canilapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PlanoAlimentar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoRacao;
    private String quantidadeGramasPorDia;
    private String frequenciaDiaria;
    private String instrucoesEspeciais;

    public PlanoAlimentar() {

    }

    public PlanoAlimentar(String tipoRacao, String quantidadeGramasPorDia, String frequenciaDiaria, String instrucoesEspeciais) {
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

    public String getQuantidadeGramasPorDia() {
        return quantidadeGramasPorDia;
    }

    public void setQuantidadeGramasPorDia(String quantidadeGramasPorDia) {
        this.quantidadeGramasPorDia = quantidadeGramasPorDia;
    }

    public String getFrequenciaDiaria() {
        return frequenciaDiaria;
    }

    public void setFrequenciaDiaria(String frequenciaDiaria) {
        this.frequenciaDiaria = frequenciaDiaria;
    }

    public String getInstrucoesEspeciais() {
        return instrucoesEspeciais;
    }

    public void setInstrucoesEspeciais(String instrucoesEspeciais) {
        this.instrucoesEspeciais = instrucoesEspeciais;
    }
}
