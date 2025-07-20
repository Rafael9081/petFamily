package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.CachorroPostRequestDTO;
import br.com.petfamily.canilapi.controller.dto.NinhadaDetalhesDTO;
import br.com.petfamily.canilapi.controller.dto.NinhadaRequestDTO;
import br.com.petfamily.canilapi.controller.dto.NinhadaResponseDTO;
import br.com.petfamily.canilapi.infra.exception.ResourceNotFoundException;
import br.com.petfamily.canilapi.mapper.NinhadaMapper;
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Ninhada;
import br.com.petfamily.canilapi.model.Sexo;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.NinhadaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NinhadaService {

    private final NinhadaRepository ninhadaRepository;
    private final CachorroRepository cachorroRepository;
    private final CachorroService cachorroService;
    private final NinhadaMapper ninhadaMapper; // Dependência adicionada

    public NinhadaService(NinhadaRepository ninhadaRepository, CachorroRepository cachorroRepository, CachorroService cachorroService, NinhadaMapper ninhadaMapper) {
        this.ninhadaRepository = ninhadaRepository;
        this.cachorroRepository = cachorroRepository;
        this.cachorroService = cachorroService;
        this.ninhadaMapper = ninhadaMapper; // Injetando o Mapper
    }

    @Transactional
    // 1. Altere o tipo de retorno do método para NinhadaResponseDTO
    public NinhadaResponseDTO criarNinhada(NinhadaRequestDTO dto) {
        Map<Long, Cachorro> paisMap = cachorroRepository.findAllById(List.of(dto.maeId(), dto.paiId()))
                .stream()
                .collect(Collectors.toMap(Cachorro::getId, Function.identity()));

        Cachorro mae = paisMap.get(dto.maeId());
        Cachorro pai = paisMap.get(dto.paiId());

        validarPais(mae, pai, dto.maeId(), dto.paiId());

        Ninhada novaNinhada = new Ninhada();
        novaNinhada.setDataNascimento(dto.dataNascimento());
        novaNinhada.setMae(mae);
        novaNinhada.setPai(pai);

        dto.filhotes().forEach(filhoteDTO -> {
            var cachorroRequest = new CachorroPostRequestDTO(
                    filhoteDTO.nome(),
                    dto.dataNascimento(),
                    mae.getRaca(),
                    filhoteDTO.sexo()
            );
            Cachorro filhoteSalvo = cachorroService.criar(cachorroRequest);
            novaNinhada.adicionarFilhote(filhoteSalvo);
        });

        // 2. Salve a entidade em uma variável local
        Ninhada ninhadaSalva = ninhadaRepository.save(novaNinhada);

        // 3. Converta a entidade salva para o DTO de resposta e retorne
        return new NinhadaResponseDTO(ninhadaSalva);
    }

    private void validarPais(Cachorro mae, Cachorro pai, Long maeId, Long paiId) {
        if (mae == null) throw new EntityNotFoundException("Cachorro (mãe) não encontrado com ID: " + maeId);
        if (pai == null) throw new EntityNotFoundException("Cachorro (pai) não encontrado com ID: " + paiId);
        if (mae.getSexo() != Sexo.FEMEA) throw new IllegalArgumentException("O cachorro selecionado como mãe (ID: " + mae.getId() + ") não é uma fêmea.");
        if (pai.getSexo() != Sexo.MACHO) throw new IllegalArgumentException("O cachorro selecionado como pai (ID: " + pai.getId() + ") não é um macho.");
        if (mae.equals(pai)) throw new IllegalArgumentException("A mãe e o pai não podem ser o mesmo cachorro.");
    }

    @Transactional(readOnly = true)
    public List<NinhadaResponseDTO> listarNinhadasDeUmaMae(Long maeId) {
        if (!cachorroRepository.existsById(maeId)) {
            throw new EntityNotFoundException("Mãe não encontrada com o ID: " + maeId);
        }
        // 1. Chama o método correto que busca as associações
        List<Ninhada> ninhadas = ninhadaRepository.findByMaeIdWithAssociations(maeId);
        // 2. Mapeia a lista de entidades para uma lista de DTOs
        return ninhadas.stream()
                .map(NinhadaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NinhadaDetalhesDTO buscarNinhadaPorId(Long id) {
        // 1. Usa a variável correta: ninhadaRepository
        Ninhada ninhada = ninhadaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ninhada não encontrada com o ID: " + id));
        // 2. Usa o mapper injetado para converter a entidade para DTO
        return ninhadaMapper.toDetalhesDTO(ninhada);
    }

    @Transactional(readOnly = true)
    public Page<NinhadaResponseDTO> listarTodasPaginado(Pageable pageable) {
        Page<Ninhada> pageDeEntidades = ninhadaRepository.findAll(pageable);
        return pageDeEntidades.map(NinhadaResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public List<NinhadaResponseDTO> listarNinhadasPorAno(int ano) {
        // 1. Chama o novo método do repositório
        List<Ninhada> ninhadas = ninhadaRepository.findByAnoNascimento(ano);
        // 2. Mapeia para DTOs
        return ninhadas.stream()
                .map(NinhadaResponseDTO::new)
                .collect(Collectors.toList());
    }
}