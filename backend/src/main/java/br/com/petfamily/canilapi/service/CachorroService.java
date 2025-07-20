package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.*;
import br.com.petfamily.canilapi.infra.exception.ResourceNotFoundException;
import br.com.petfamily.canilapi.model.*;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.NinhadaRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;


@Service
public class CachorroService {

    private final CachorroRepository cachorroRepository;
    private final TutorRepository tutorRepository;
    private final ObjectMapper objectMapper;
    private final NinhadaRepository ninhadaRepository;

    public CachorroService(CachorroRepository cachorroRepository,NinhadaRepository ninhadaRepository, TutorRepository tutorRepository, ObjectMapper objectMapper) {
        this.cachorroRepository = cachorroRepository;
        this.tutorRepository = tutorRepository;
        this.objectMapper = objectMapper;
        this.ninhadaRepository = ninhadaRepository;
    }

    public Cachorro buscarPorId(Long id) {
        return cachorroRepository.findByIdWithAssociations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cachorro não encontrado com ID: " + id));
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
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com ID: " + dto.tutorId()));

        Cachorro novoCachorro = new Cachorro();
        novoCachorro.setNome(dto.nome());
        novoCachorro.setRaca(dto.raca());
        novoCachorro.setDataNascimento(dto.dataNascimento());
        novoCachorro.setSexo(dto.sexo());
        novoCachorro.setTutor(tutor);
        novoCachorro.setStatus(StatusCachorro.DISPONIVEL);
        return cachorroRepository.save(novoCachorro);
    }

    @Transactional
    public Cachorro criar(CachorroPostRequestDTO dto) {
        Cachorro novoCachorro = new Cachorro();
        novoCachorro.setNome(dto.nome());
        novoCachorro.setRaca(dto.raca());
        novoCachorro.setDataNascimento(dto.dataNascimento());
        novoCachorro.setSexo(dto.sexo());
        novoCachorro.setStatus(StatusCachorro.DISPONIVEL);
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
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com ID: " + dto.tutorId()));

        // Atualiza os campos da entidade com os dados do DTO
        cachorro.setNome(dto.nome());
        cachorro.setRaca(dto.raca());
        cachorro.setDataNascimento(dto.dataNascimento());
        cachorro.setSexo(dto.sexo());
        cachorro.setTutor(tutor);

        // O save em uma entidade já existente funciona como um update
        return cachorroRepository.save(cachorro);
    }

    public Page<CachorroResponseDTO> listarTodosPaginado(Pageable pageable) {
        // 1. Busca a página de entidades (sem as coleções ToMany)
        Page<Cachorro> cachorrosPage = cachorroRepository.findByPage(pageable);

        // 2. Extrai os IDs da página atual de forma mais concisa
        List<Long> ids = cachorrosPage.map(Cachorro::getId).getContent();

        if (ids.isEmpty()) {
            return Page.empty(pageable);
        }

        // 3. Busca as entidades completas com as coleções, mas apenas para os IDs da página
        List<Cachorro> cachorrosCompletos = cachorroRepository.findAllWithCollectionsByIds(ids);

        // 4. Mapeia para DTOs. A mágica para evitar o erro acontece dentro do construtor do DTO
        List<CachorroResponseDTO> dtos = cachorrosCompletos.stream()
                .map(CachorroResponseDTO::new)
                .collect(Collectors.toList());

        // 5. Retorna um novo PageImpl com o contedo completo e a paginação original
        return new PageImpl<>(dtos, pageable, cachorrosPage.getTotalElements());
    }


    @Transactional(readOnly = true)
    public RelatorioFinanceiroDTO gerarRelatorioFinanceiro(Long id) {
        Cachorro cachorro = this.buscarPorId(id);

        // 1. Mapeia a lista de despesas para a lista de DTOs
        List<DespesaInfoDTO> despesasDTO = cachorro.getHistoricoDespesas().stream()
                .map(DespesaInfoDTO::new)
                .toList(); // .toList() é mais moderno que .collect(Collectors.toList())

        // 2. Calcula o custo total usando BigDecimal para garantir a precisão
        BigDecimal custoTotal = despesasDTO.stream()
                .map(DespesaInfoDTO::valor) // Acessa o valor BigDecimal do DTO
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Prepara as variáveis que dependem da venda
        VendaResponseDTO vendaDTO = null;
        BigDecimal lucro = null;

        if (cachorro.isFoiVendido() && cachorro.getRegistroVenda() != null) {
            vendaDTO = new VendaResponseDTO(cachorro.getRegistroVenda());
            // Calcula o lucro usando o método subtract() do BigDecimal
            lucro = vendaDTO.valor().subtract(custoTotal);
        }

        // 4. Cria o DTO final usando o construtor do record, de uma só vez
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
            objectMapper.updateValue(cachorroParaAtualizar, campos);

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
    public List<Ninhada> listarNinhadasDaMae(Long maeId) {
        if (!cachorroRepository.existsById(maeId)) {
            throw new ResourceNotFoundException("Cachorro com ID " + maeId + " não encontrado.");
        }

        return ninhadaRepository.findByMaeIdWithAssociations(maeId);
    }

    @Transactional(readOnly = true)
    public CachorroResponseDTO buscarDTOPorId(Long id) {
        Cachorro cachorro = this.buscarPorId(id); // Reutiliza sua busca que já tem o JOIN FETCH
        return new CachorroResponseDTO(cachorro); // Converte a entidade para o DTO
    }

}