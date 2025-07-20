package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.DespesaRequestDTO; // Usando o DTO
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.CategoriaDespesa;
import br.com.petfamily.canilapi.model.Despesa;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.DespesaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DespesaService {

    private final DespesaRepository despesaRepository;
    private final CachorroRepository cachorroRepository;

    // 1. Injeção de dependência via construtor (melhor prática)
    public DespesaService(DespesaRepository despesaRepository, CachorroRepository cachorroRepository) {
        this.despesaRepository = despesaRepository;
        this.cachorroRepository = cachorroRepository;
    }

    @Transactional
    // 2. Método agora recebe o DTO, tornando-o seguro e consistente
    public Despesa registrarDespesa(Long cachorroId, DespesaRequestDTO dto) {
        Cachorro cachorro = cachorroRepository.findById(cachorroId)
                .orElseThrow(() -> new EntityNotFoundException("Cachorro não encontrado com o ID: " + cachorroId));

        // 3. A validação manual foi removida, pois agora é tratada pelo @Valid no controller
        Despesa novaDespesa = new Despesa();
        novaDespesa.setDescricao(dto.descricao());
        novaDespesa.setValor(dto.valor());
        novaDespesa.setCachorro(cachorro);

        // A lógica para definir a data padrão permanece, o que é ótimo
        novaDespesa.setData(dto.data() != null ? dto.data() : LocalDate.now());

        return despesaRepository.save(novaDespesa);
    }

    @Transactional
    public void deletarDespesa(Long despesaId) {
        // Uma pequena melhoria: buscar o objeto primeiro garante que ele existe
        // e é mais consistente com o padrão "buscar-ou-falhar".
        Despesa despesa = despesaRepository.findById(despesaId)
                .orElseThrow(() -> new EntityNotFoundException("Despesa não encontrada com o ID: " + despesaId));
        despesaRepository.delete(despesa);
    }

    public List<Despesa> listarDespesasDeUmCachorro(long cachorroId) {
        // Validação para garantir que o cachorro existe antes de listar suas despesas
        if (!cachorroRepository.existsById(cachorroId)) {
            throw new EntityNotFoundException("Cachorro não encontrado com o ID: " + cachorroId);
        }
        return despesaRepository.findByCachorro_Id(cachorroId);
    }

    // Nenhuma alteração necessária nos métodos abaixo, eles já estão bem implementados.
    public List<Despesa> listarDespesasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return despesaRepository.findAllByDataBetween(dataInicio, dataFim);
    }


    public BigDecimal calcularCustoTotalPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        BigDecimal total = despesaRepository.sumDespesasByPeriodo(dataInicio, dataFim);
        // A verificação de nulo agora retorna BigDecimal.ZERO, mantendo a consistência.
        return total == null ? BigDecimal.ZERO : total;
    }
}