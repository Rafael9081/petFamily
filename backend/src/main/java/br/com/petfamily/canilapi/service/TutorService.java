package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.TutorResponseDTO;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TutorService {

    @Autowired
    private TutorRepository tutorRepository;

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
    public Tutor criar(Tutor tutor) {
        if (tutor.getNome() == null || tutor.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do tutor não pode ser vazio.");
        }
        return tutorRepository.save(tutor);
    }

    @Transactional
    public Tutor atualizar(Long id, Tutor dadosTutor) {
        Tutor tutorExistente = buscarPorId(id); // Reutiliza a busca que já lança exceção

        if (dadosTutor.getNome() != null && !dadosTutor.getNome().trim().isEmpty()) {
            tutorExistente.setNome(dadosTutor.getNome());
        }
        if (dadosTutor.getEmail() != null) {
            tutorExistente.setEmail(dadosTutor.getEmail());
        }
        if (dadosTutor.getTelefone() != null) {
            tutorExistente.setTelefone(dadosTutor.getTelefone());
        }

        return tutorRepository.save(tutorExistente);
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
