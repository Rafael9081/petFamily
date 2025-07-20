package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.TutorRequestDTO;
import br.com.petfamily.canilapi.controller.dto.TutorResponseDTO;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TutorService {

    private final  TutorRepository tutorRepository;
    private final CachorroRepository cachorroRepository;
    private final ObjectMapper objectMapper;

    public TutorService(TutorRepository tutorRepository, CachorroRepository cachorroRepository) {
        this.tutorRepository = tutorRepository;
        this.cachorroRepository = cachorroRepository;
        this.objectMapper = new ObjectMapper(); // Inicializa o ObjectMapper
    }

    public List<Tutor> listarTodos() {
        return tutorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TutorResponseDTO> listarTodosDTO() {
        return tutorRepository.findAll().stream()
                .map(TutorResponseDTO::new) // O mapeamento acontece aqui, dentro da transação
                .collect(Collectors.toList());
    }

    public Tutor buscarPorId(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tutor não encontrado com o ID: " + id));
    }

    @Transactional
    public Tutor criar(TutorRequestDTO dto) {
        Tutor novoTutor = new Tutor();
        // 2. Preenche a entidade com os dados que vieram do DTO.
        novoTutor.setNome(dto.nome());
        novoTutor.setEmail(dto.email());
        novoTutor.setTelefone(dto.telefone());
        // 3. Salva a nova entidade no banco.
        return tutorRepository.save(novoTutor);
    }

    @Transactional
    public TutorResponseDTO atualizar(Long id, TutorRequestDTO dto) {
        Tutor tutorExistente = tutorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tutor não encontrado com o ID: " + id));

        tutorExistente.setNome(dto.nome());
        tutorExistente.setEmail(dto.email());
        tutorExistente.setTelefone(dto.telefone());

        return new TutorResponseDTO(tutorExistente);
    }

    @Transactional
    public void deletar(Long id) {
        Tutor tutor = buscarPorId(id);
        if (tutor.getCachorros() != null && !tutor.getCachorros().isEmpty()) {
            throw new IllegalStateException("Não é possível deletar um tutor que possui cachorros associados.");
        }

        tutorRepository.delete(tutor);
    }

    public Tutor buscarTutorPorNome(String nome) {

        return tutorRepository.findByNome(nome);
    }

    @Transactional(readOnly = true)
    public TutorResponseDTO buscarPorIdDTO(Long id) {
        Tutor tutor = this.buscarPorId(id); // Reutiliza a lógica existente
        return new TutorResponseDTO(tutor);
    }

    @Transactional(readOnly = true)
    public Page<TutorResponseDTO> listarTodosPaginado(Pageable pageable) {
        Page<Tutor> paginaDeTutores = tutorRepository.findAll(pageable);
        return paginaDeTutores.map(TutorResponseDTO::new);
    }

    @Transactional
    public Tutor atualizarParcial(Long id, Map<String, Object> campos) {
        // 1. Busca o tutor no banco de dados. Se não encontrar, lança uma exceção.
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com o id: " + id)); // Idealmente, use uma exceção customizada (ex: ResourceNotFoundException)

        // 2. Itera sobre os campos recebidos no JSON
        campos.forEach((nomeCampo, valorCampo) -> {
            // 3. Usa Reflection para encontrar o campo correspondente na classe Tutor
            Field field = ReflectionUtils.findField(Tutor.class, nomeCampo);

            if (field != null) {
                // Permite o acesso a campos privados
                field.setAccessible(true);

                // Converte o valor recebido (ex: de um Integer para um Long) para o tipo exato do campo na entidade
                Object valorConvertido = objectMapper.convertValue(valorCampo, field.getType());

                // Define o novo valor no campo do objeto 'tutor'
                ReflectionUtils.setField(field, tutor, valorConvertido);
            }
        });

        // 4. Salva o tutor com os campos atualizados (dentro de uma transação, o save pode ser opcional dependendo da configuração)
        return tutorRepository.save(tutor);
    }
}

