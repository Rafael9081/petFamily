package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TutorService {

    @Autowired
    private TutorRepository tutorRepository;

    @Transactional
    public Tutor criarTutor(Tutor tutor) {
        if (tutor.getNome() == null || tutor.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do tutor não pode ser vazio.");
        }
        return tutorRepository.save(tutor);
    }

    public List<Tutor> listarTodosTutores() {
        return tutorRepository.findAll();
    }

    public Tutor buscarTutorPorId(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tutor não encontrado com o ID: " + id));
    }

    public Tutor buscarTutorPorNome(String nome) {
        return tutorRepository.findByNome(nome);
    }

    @Transactional
    public Tutor atualizarDadosTutor(Long id, Tutor dadosAtualizados) {
        Tutor tutorExistente = buscarTutorPorId(id);
        if (tutorExistente == null) {
            throw new IllegalArgumentException("Tutor não encontrado.");
        }

        if (dadosAtualizados.getNome() != null && !dadosAtualizados.getNome().isBlank()) {
            tutorExistente.setNome(dadosAtualizados.getNome());
        }

        if (dadosAtualizados.getEmail() != null && !dadosAtualizados.getEmail().isBlank()) {
            tutorExistente.setEmail(dadosAtualizados.getEmail());
        }

        if (dadosAtualizados.getTelefone() != null && !dadosAtualizados.getTelefone().isBlank()) {
            tutorExistente.setTelefone(dadosAtualizados.getTelefone());
        }

        return tutorRepository.save(tutorExistente);
    }
}
