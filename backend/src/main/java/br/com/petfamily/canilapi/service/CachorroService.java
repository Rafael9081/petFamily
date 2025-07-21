package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.*;
import br.com.petfamily.canilapi.infra.exception.ResourceNotFoundException;
import br.com.petfamily.canilapi.model.*;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CachorroService {

    private final CachorroRepository cachorroRepository;
    private final TutorRepository tutorRepository;
    private final ObjectMapper objectMapper;
    // A dependência do NinhadaRepository foi removida para centralizar a responsabilidade.

    public CachorroService(CachorroRepository cachorroRepository, TutorRepository tutorRepository, ObjectMapper objectMapper) {
        this.cachorroRepository = cachorroRepository;
        this.tutorRepository = tutorRepository;
        this.objectMapper = objectMapper;
    }

    public Cachorro buscarPorId(Long id) {
        return cachorroRepository.findByIdWithAssociations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cachorro não encontrado com ID: " + id));
    }

    @Transactional
    public void deletarCachorro(Long id) {
        // A busca garante que o cachorro existe antes de tentar deletar
        this.buscarPorId(id);
        cachorroRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Cachorro> listarTodos() {
        return cachorroRepository.findAllWithAssociations();
    }

    // --- MÉTODOS 'CRIAR' CONSOLIDADOS ---

    @Transactional
    public Cachorro criar(CachorroRequestDTO dto) {
        Tutor tutor = tutorRepository.findById(dto.tutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com ID: " + dto.tutorId()));
        // Chama o mo com a lógica comum
        return criarCachorroComum(dto.nome(), dto.raca(), dto.dataNascimento(), dto.sexo(), tutor);
    }

    @Transactional
    public Cachorro criar(CachorroPostRequestDTO dto) {
        // Chama o método privado com a lógica comum, passando null para o tutor
        return criarCachorroComum(dto.nome(), dto.raca(), dto.dataNascimento(), dto.sexo(), null);
    }

    /**
     * Método privado que centraliza a lógica de criação de um cachorro, evitando duplicação de código.
     */
    private Cachorro criarCachorroComum(String nome, String raca, LocalDate dataNascimento, Sexo sexo, Tutor tutor) {
        Cachorro novoCachorro = new Cachorro();
        novoCachorro.setNome(nome);
        novoCachorro.setRaca(raca);
        novoCachorro.setDataNascimento(dataNascimento);
        novoCachorro.setSexo(sexo);
        novoCachorro.setStatus(StatusCachorro.DISPONIVEL); // Regra de negócio centralizada
        if (tutor != null) {
            novoCachorro.setTutor(tutor);
        }
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

        // RETORNO SEGURO: Retorna a instância da despesa que foi adicionada.
        // O JPA já populou o ID e o estado dela após o save.
        return novaDespesa;
    }

    @Transactional
    public Cachorro atualizar(Long id, CachorroRequestDTO dto) {
        Cachorro cachorro = this.buscarPorId(id);
        Tutor tutor = tutorRepository.findById(dto.tutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com ID: " + dto.tutorId()));

        cachorro.setNome(dto.nome());
        cachorro.setRaca(dto.raca());
        cachorro.setDataNascimento(dto.dataNascimento());
        cachorro.setSexo(dto.sexo());
        cachorro.setTutor(tutor);

        return cachorroRepository.save(cachorro);
    }

    public Page<CachorroResponseDTO> listarTodosPaginado(Pageable pageable) {
        Page<Cachorro> cachorrosPage = cachorroRepository.findByPage(pageable);
        List<Long> ids = cachorrosPage.map(Cachorro::getId).getContent();

        if (ids.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Cachorro> cachorrosCompletos = cachorroRepository.findAllWithCollectionsByIds(ids);
        List<CachorroResponseDTO> dtos = cachorrosCompletos.stream()
                .map(CachorroResponseDTO::new)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, cachorrosPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public RelatorioFinanceiroDTO gerarRelatorioFinanceiro(Long id) {
        Cachorro cachorro = this.buscarPorId(id);

        List<DespesaInfoDTO> despesasDTO = cachorro.getHistoricoDespesas().stream()
                .map(DespesaInfoDTO::new)
                .toList();

        BigDecimal custoTotal = despesasDTO.stream()
                .map(DespesaInfoDTO::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        VendaResponseDTO vendaDTO = null;
        BigDecimal lucro = null;

        if (cachorro.isFoiVendido() && cachorro.getRegistroVenda() != null) {
            vendaDTO = new VendaResponseDTO(cachorro.getRegistroVenda());
            lucro = vendaDTO.valor().subtract(custoTotal);
        }

        return new RelatorioFinanceiroDTO(
                cachorro.getId(),
                cachorro.getNome(),
                despesasDTO,
                custoTotal,
                cachorro.isFoiVendido(),
                vendaDTO,
                lucro
        );
    }

    @Transactional
    public Cachorro atualizarParcial(Long id, Map<String, Object> campos) {
        Cachorro cachorroParaAtualizar = buscarPorId(id);

        try {
            // O ObjectMapper pode atualizar campos aninhados se a estrutura do JSON corresponder
            objectMapper.updateValue(cachorroParaAtualizar, campos);

            // Tratamento especial para relacionamentos que precisam ser buscados
            if (campos.containsKey("tutorId")) {
                Long novoTutorId = Long.valueOf(campos.get("tutorId").toString());
                Tutor novoTutor = tutorRepository.findById(novoTutorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com ID: " + novoTutorId));
                cachorroParaAtualizar.setTutor(novoTutor);
            }
            return cachorroRepository.save(cachorroParaAtualizar);

        } catch (MismatchedInputException e) {
            throw new IllegalStateException("Tipo de dado inválido para o campo: " + e.getPath().get(0).getFieldName(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar os dados do cachorro.", e);
        }
    }

    @Transactional(readOnly = true)
    public CachorroResponseDTO buscarDTOPorId(Long id) {
        Cachorro cachorro = this.buscarPorId(id);
        return new CachorroResponseDTO(cachorro);
    }

    @Transactional
    public Cachorro atualizarStatus(Long id, StatusCachorro novoStatus) {
        Cachorro cachorro = this.buscarPorId(id);

        // Future-proof: This is the perfect place to add validation for status transitions.
        // For example: if (cachorro.getStatus() == StatusCachorro.VENDIDO) { ... }

        cachorro.setStatus(novoStatus);
        return cachorroRepository.save(cachorro);
    }

}