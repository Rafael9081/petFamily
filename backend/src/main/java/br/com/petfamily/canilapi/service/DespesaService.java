package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.CategoriaDespesa;
import br.com.petfamily.canilapi.model.Despesa;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.DespesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DespesaService {

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private CachorroRepository cachorroRepository;

    @Transactional
    public Despesa registrarDespesa(Long cachorroId, Despesa dadosDespesa) {
        Cachorro cachorro = cachorroRepository.findById(cachorroId)
                .orElseThrow(() -> new RuntimeException("Cachorro não encontrado com o ID: " + cachorroId));

        if (dadosDespesa.getValor() <= 0) {
            throw new IllegalArgumentException("O valor da despesa deve ser maior que zero.");
        }

        if (dadosDespesa.getDescricao() == null || dadosDespesa.getDescricao().isBlank()) {
            throw new IllegalArgumentException("A descrição da despesa não pode ser vazia.");
        }

        dadosDespesa.setCachorro(cachorro);
        if (dadosDespesa.getData() == null) {
            dadosDespesa.setData(LocalDate.now());
        }

        return despesaRepository.save(dadosDespesa);
    }

    @Transactional
    public void deletarDespesa(Long despesaId) {
        if (!despesaRepository.existsById(despesaId)) {
            throw new RuntimeException("Despesa não encontrada com o ID: " + despesaId);
        }
        despesaRepository.deleteById(despesaId);
    }

    public List<Despesa> listarDespesasDeUmCachorro(long cachorroId) {
        return despesaRepository.findByCachorro_Id(cachorroId);
    }

    public List<Despesa> listarDespesasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return despesaRepository.findAllByDataBetween(dataInicio, dataFim);
    }

    public List<Despesa> listarDespesasPorCategoria(CategoriaDespesa categoria) {
        return despesaRepository.findAllByCategoria(categoria);
    }

    public Double calcularCustoTotalPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        Double total = despesaRepository.sumDespesasByPeriodo(dataInicio, dataFim);
        return total == null ? 0.0 : total;
    }
    
}
