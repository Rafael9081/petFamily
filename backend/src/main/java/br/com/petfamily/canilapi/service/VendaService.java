package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.VendaRequestDTO;
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import br.com.petfamily.canilapi.repository.VendaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final CachorroRepository cachorroRepository;
    private final TutorRepository tutorRepository;

    public VendaService(VendaRepository vendaRepository, CachorroRepository cachorroRepository, TutorRepository tutorRepository) {
        this.vendaRepository = vendaRepository;
        this.cachorroRepository = cachorroRepository;
        this.tutorRepository = tutorRepository;
    }

    @Transactional
    public Venda realizarVenda(long cachorroId, VendaRequestDTO dto) {
        Cachorro cachorro = cachorroRepository.findByIdWithAssociations(cachorroId)
                .orElseThrow(() -> new EntityNotFoundException("Cachorro não encontrado com ID: " + cachorroId));

        // MELHORIA: Adicionando uma regra de negócio crucial
        if (cachorro.isFoiVendido()) {
            throw new IllegalStateException("Este cachorro já foi vendido e não pode ser vendido novamente.");
        }

        Tutor novoTutor = tutorRepository.findById(dto.novoTutorId())
                .orElseThrow(() -> new EntityNotFoundException("Tutor não encontrado com ID: " + dto.novoTutorId()));

        Venda novaVenda = new Venda(dto.valor(), dto.dataVenda(), cachorro, novoTutor);
        cachorro.setFoiVendido(true);
        cachorro.setTutor(novoTutor);
        cachorro.setRegistroVenda(novaVenda);

        // O cachorro será atualizado em cascata, mas salvamos a venda explicitamente.
        return vendaRepository.save(novaVenda);
    }
}
