package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.TutorRequestDTO;
import br.com.petfamily.canilapi.controller.dto.TutorResponseDTO;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorService {

    private final  TutorRepository tutorRepository;

    public TutorService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @Autowired
    private CachorroRepository cachorroRepository;

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
    public Tutor atualizar(Long id, TutorRequestDTO dto) {
        Tutor tutorExistente = tutorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tutor não encontrado com o ID: " + id));

        tutorExistente.setNome(dto.nome());
        tutorExistente.setEmail(dto.email());

        return tutorExistente;
    }

    @Transactional
    public void deletar(Long id) {
        Tutor tutor = buscarPorId(id);

        // REGRA DE NEGÓCIO: Não permitir exclusão se o tutor tiver cachorros associados.
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

}
