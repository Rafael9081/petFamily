package br.com.petfamily.canilapi.dto;

import br.com.petfamily.canilapi.controller.dto.DespesaInfoDTO;
import br.com.petfamily.canilapi.controller.dto.VendaResponseDTO;
import br.com.petfamily.canilapi.model.Venda;

import java.util.List;

// Este DTO não é uma entidade, é apenas um objeto para transferir dados.
public class RelatorioFinanceiroDTO {

    private Long cachorroId;
    private String nomeCachorro;
    private List<DespesaInfoDTO> historicoDespesas;
    private double custoTotal;
    private boolean foiVendido;
    private VendaResponseDTO registroVenda;
    private Double lucro; // Usamos Double (objeto) para permitir valor nulo se não foi vendido

    // Construtor, Getters e Setters...

    public RelatorioFinanceiroDTO() {
    }

    public Long getCachorroId() {
        return cachorroId;
    }

    public void setCachorroId(Long cachorroId) {
        this.cachorroId = cachorroId;
    }

    public String getNomeCachorro() {
        return nomeCachorro;
    }

    public void setNomeCachorro(String nomeCachorro) {
        this.nomeCachorro = nomeCachorro;
    }

    public List<DespesaInfoDTO> getHistoricoDespesas() {
        return historicoDespesas;
    }

    public void setHistoricoDespesas(List<DespesaInfoDTO> historicoDespesas) {
        this.historicoDespesas = historicoDespesas;
    }

    public double getCustoTotal() {
        return custoTotal;
    }

    public void setCustoTotal(double custoTotal) {
        this.custoTotal = custoTotal;
    }

    public boolean isFoiVendido() {
        return foiVendido;
    }

    public void setFoiVendido(boolean foiVendido) {
        this.foiVendido = foiVendido;
    }


    public VendaResponseDTO getRegistroVenda() {
        return registroVenda;
    }

    public void setRegistroVenda(VendaResponseDTO registroVenda) {
        this.registroVenda = registroVenda;
    }

    public Double getLucro() {
        return lucro;
    }

    public void setLucro(Double lucro) {
        this.lucro = lucro;
    }
}