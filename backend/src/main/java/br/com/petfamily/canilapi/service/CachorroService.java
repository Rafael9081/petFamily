package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.DespesaInfoDTO;
import br.com.petfamily.canilapi.controller.dto.VendaResponseDTO;
import br.com.petfamily.canilapi.controller.dto.CachorroRequestDTO;
import br.com.petfamily.canilapi.controller.dto.DespesaRequestDTO;
import br.com.petfamily.canilapi.dto.RelatorioFinanceiroDTO; // 1. Importe o novo DTO
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Despesa;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.io.IOException;

@Service
public class CachorroService {

    private final CachorroRepository cachorroRepository;
    private final TutorRepository tutorRepository;
    private final ObjectMapper objectMapper;

    public CachorroService(CachorroRepository cachorroRepository, TutorRepository tutorRepository, ObjectMapper objectMapper) {
        this.cachorroRepository = cachorroRepository;
        this.tutorRepository = tutorRepository;
        this.objectMapper = objectMapper;
    }

    public Cachorro buscarPorId(Long id) {
        return cachorroRepository.findByIdWithAssociations(id)
                .orElseThrow(() -> new EntityNotFoundException("Cachorro não encontrado com ID: " + id));
    }

    @Transactional
    public void deletarCachorro(Long id) {
        this.buscarPorId(id);
        cachorroRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Cachorro> listarTodos() {
        return cachorroRepository.findAllWithAssociations();
    }

    @Transactional
    public Cachorro criar(CachorroRequestDTO dto) {
        Tutor tutor = tutorRepository.findById(dto.tutorId())
                .orElseThrow(() -> new EntityNotFoundException("Tutor não encontrado com ID: " + dto.tutorId()));

        Cachorro novoCachorro = new Cachorro();
        novoCachorro.setNome(dto.nome());
        novoCachorro.setRaca(dto.raca());
        novoCachorro.setDataNascimento(dto.dataNascimento());
        novoCachorro.setSexo(dto.sexo());
        novoCachorro.setTutor(tutor);

        return cachorroRepository.save(novoCachorro);
    }

    @Transactional
    public Despesa adicionarDespesa(Long cachorroId, DespesaRequestDTO dto) {
        Cachorro cachorro = buscarPorId(cachorroId);

        Despesa novaDespesa = new Despesa();
        novaDespesa.setDescricao(dto.descricao());
        novaDespesa.setValor(dto.valor());
        novaDespesa.setData(dto.data() != null ? dto.data() : LocalDate.now());

        cachorro.adicionarDespesa(novaDespesa);
        cachorroRepository.save(cachorro);

        return cachorro.getHistoricoDespesas().get(cachorro.getHistoricoDespesas().size() - 1);
    }

    @Transactional
    public Cachorro atualizar(Long id, CachorroRequestDTO dto) {
        // Reutiliza a busca que já lança exceção se não encontrar
        Cachorro cachorro = this.buscarPorId(id);

        // Busca o tutor para garantir que ele existe
        Tutor tutor = tutorRepository.findById(dto.tutorId())
                .orElseThrow(() -> new EntityNotFoundException("Tutor não encontrado com ID: " + dto.tutorId()));

        // Atualiza os campos da entidade com os dados do DTO
        cachorro.setNome(dto.nome());
        cachorro.setRaca(dto.raca());
        cachorro.setDataNascimento(dto.dataNascimento());
        cachorro.setSexo(dto.sexo());
        cachorro.setTutor(tutor);

        // O save em uma entidade já existente funciona como um update
        return cachorroRepository.save(cachorro);
    }

    @Transactional(readOnly = true)
    public Page<Cachorro> listarTodosPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cachorro> cachorrosPage = cachorroRepository.findAllWithTutor(pageable);
        cachorrosPage.getContent().forEach(cachorro -> cachorro.getHistoricoDespesas().size());

        return cachorrosPage;
    }


    @Transactional(readOnly = true)
    public RelatorioFinanceiroDTO gerarRelatorioFinanceiro(Long id) {
        Cachorro cachorro = this.buscarPorId(id);

        double custoTotal = cachorro.getHistoricoDespesas().stream()
                .mapToDouble(Despesa::getValor)
                .sum();

        // --- MUDANÇA AQUI: Convertendo para o DTO existente ---
        List<DespesaInfoDTO> despesasDTO = cachorro.getHistoricoDespesas().stream()
                .map(DespesaInfoDTO::new) // Usa o construtor que adicionamos
                .collect(Collectors.toList());

        RelatorioFinanceiroDTO relatorio = new RelatorioFinanceiroDTO();
        relatorio.setCachorroId(cachorro.getId());
        relatorio.setNomeCachorro(cachorro.getNome());
        relatorio.setHistoricoDespesas(despesasDTO); // <-- Agora passa a lista correta
        relatorio.setCustoTotal(custoTotal);
        relatorio.setFoiVendido(cachorro.isFoiVendido());

        if (cachorro.isFoiVendido() && cachorro.getRegistroVenda() != null) {
            VendaResponseDTO vendaDTO = new VendaResponseDTO(cachorro.getRegistroVenda());
            relatorio.setRegistroVenda(vendaDTO);

            double valorVenda = cachorro.getRegistroVenda().getValor();
            relatorio.setLucro(valorVenda - custoTotal);
        }

        return relatorio;
    }

    @Transactional
    public Cachorro atualizarParcial(Long id, Map<String, Object> campos) {
        Cachorro cachorroSalvo = buscarPorId(id);

        try {
            Cachorro cachorroAtualizado = objectMapper.updateValue(cachorroSalvo, campos);

            if (campos.containsKey("tutorId")) {
                Long novoTutorId = Long.valueOf(campos.get("tutorId").toString());
                Tutor novoTutor = tutorRepository.findById(novoTutorId)
                        .orElseThrow(() -> new EntityNotFoundException("Tutor não encontrado com ID: " + novoTutorId));
                cachorroAtualizado.setTutor(novoTutor);
            }

            return this.buscarPorId(id);
        } catch (MismatchedInputException e) {
            throw new IllegalStateException("Tipo de dado inválido para o campo: " + e.getPath().get(0).getFieldName(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar os dados do cachorro.", e);
        }
    }
}