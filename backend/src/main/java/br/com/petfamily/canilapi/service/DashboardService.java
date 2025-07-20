package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.AtividadeRecenteDTO;
import br.com.petfamily.canilapi.controller.dto.DashboardStatsDTO;
import br.com.petfamily.canilapi.controller.dto.FinanceiroDashboardDTO;
import br.com.petfamily.canilapi.model.Despesa;
import br.com.petfamily.canilapi.model.StatusCachorro;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.repository.*;
import br.com.petfamily.canilapi.repository.projection.AtividadeRecenteProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    // Dependências que ainda são necessárias para outros métodos
    private final VendaRepository vendaRepository;
    private final DespesaRepository despesaRepository;
    private final CachorroRepository cachorroRepository;
    private final TutorRepository tutorRepository;

    // A nova dependência centralizadora
    private final AtividadeRepository atividadeRepository;

    // O NinhadaRepository foi removido do construtor
    public DashboardService(VendaRepository vendaRepository, DespesaRepository despesaRepository,
                            CachorroRepository cachorroRepository, TutorRepository tutorRepository,
                            AtividadeRepository atividadeRepository) {
        this.vendaRepository = vendaRepository;
        this.despesaRepository = despesaRepository;
        this.cachorroRepository = cachorroRepository;
        this.tutorRepository = tutorRepository;
        this.atividadeRepository = atividadeRepository;
    }

    public DashboardStatsDTO getStats() {
        long caesDisponiveis = cachorroRepository.countByStatus(StatusCachorro.DISPONIVEL);
        long totalTutores = tutorRepository.count();
        return new DashboardStatsDTO(caesDisponiveis, totalTutores);
    }

    @Transactional(readOnly = true)
    public FinanceiroDashboardDTO getFinanceiroUltimos30Dias() {
        LocalDate dataInicio = LocalDate.now().minusDays(30);

        // 1. Correção: Some os BigDecimals usando map e reduce para manter a precisão.
        BigDecimal receita = vendaRepository.findVendasAfter(dataInicio).stream()
                .map(Venda::getValor) // Mapeia para um Stream<BigDecimal>
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Soma usando o método add

        BigDecimal despesa = despesaRepository.findDespesasAfter(dataInicio).stream()
                .map(Despesa::getValor) // Mapeia para um Stream<BigDecimal>
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Soma usando o método add

        // 2. Correção: Use o método .subtract() para subtrair BigDecimals.
        BigDecimal lucro = receita.subtract(despesa);

        return new FinanceiroDashboardDTO(receita, despesa, lucro);
    }

    @Transactional(readOnly = true)
    public List<AtividadeRecenteDTO> getAtividadesRecentes() {
        List<AtividadeRecenteProjection> projecoes = atividadeRepository.findAtividadesRecentes();

        return projecoes.stream()
                .map(p -> new AtividadeRecenteDTO(
                        p.getTipo(),
                        p.getDescricao(),
                        p.getData(),
                        p.getEntidadeId()
                ))
                .collect(Collectors.toList());
    }
}