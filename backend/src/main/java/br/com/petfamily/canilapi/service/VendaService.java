package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.VendaRequestDTO;
import br.com.petfamily.canilapi.controller.dto.VendaRequestFlexDTO;
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.StatusCachorro;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import br.com.petfamily.canilapi.repository.VendaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final CachorroRepository cachorroRepository;
    private final TutorRepository tutorRepository;
    private final TutorService tutorService; // Adicionamos a dependência do TutorService

    // Construtor atualizado para injetar todas as dependências necessárias
    public VendaService(VendaRepository vendaRepository, CachorroRepository cachorroRepository, TutorRepository tutorRepository, TutorService tutorService) {
        this.vendaRepository = vendaRepository;
        this.cachorroRepository = cachorroRepository;
        this.tutorRepository = tutorRepository;
        this.tutorService = tutorService;
    }

    // --- LÓGICA ANTIGA (Ainda pode ser útil ou pode ser removida) ---
    @Transactional
    public Venda realizarVenda(long cachorroId, VendaRequestDTO dto) {
        Cachorro cachorro = cachorroRepository.findById(cachorroId)
                .orElseThrow(() -> new EntityNotFoundException("Cachorro não encontrado com ID: " + cachorroId));

        Tutor tutor = tutorRepository.findById(dto.novoTutorId())
                .orElseThrow(() -> new EntityNotFoundException("Tutor não encontrado com ID: " + dto.novoTutorId()));

        if (cachorro.getStatus() == StatusCachorro.VENDIDO) {
            throw new IllegalStateException("Este cachorro já foi vendido.");
        }

        Venda novaVenda = new Venda();
        novaVenda.setCachorro(cachorro);
        novaVenda.setNovoTutor(tutor);
        novaVenda.setValor(dto.valor());
        novaVenda.setDataVenda(dto.data());

        cachorro.setStatus(StatusCachorro.VENDIDO);
        cachorro.setTutor(tutor);

        return vendaRepository.save(novaVenda);
    }

    @Transactional
    public Venda realizarVendaFlex(Long cachorroId, VendaRequestFlexDTO dto) {
        // 1. Validações iniciais
        Cachorro cachorro = cachorroRepository.findById(cachorroId)
                .orElseThrow(() -> new EntityNotFoundException("Cachorro não encontrado com ID: " + cachorroId));

        StatusCachorro Status;
        if (cachorro.getStatus() == StatusCachorro.VENDIDO) {
            throw new IllegalStateException("Este cachorro já foi vendido.");
        }

        Tutor tutorDaVenda;

        // 2. O coração da lógica "Flex": decidir entre buscar ou criar o tutor
        if (dto.tutorId() != null) {
            // Cenário A: Vendendo para um tutor existente
            tutorDaVenda = tutorRepository.findById(dto.tutorId())
                    .orElseThrow(() -> new EntityNotFoundException("Tutor existente não encontrado com ID: " + dto.tutorId()));
        } else {
            // Cenário B: Vendendo para um novo tutor
            // Delegamos a criação para o TutorService, mantendo a responsabilidade separada
            tutorDaVenda = tutorService.criar(dto.novoTutor());
        }

        // 3. Preparar e registrar a venda
        Venda novaVenda = new Venda();
        novaVenda.setCachorro(cachorro);
        novaVenda.setNovoTutor(tutorDaVenda);
        novaVenda.setValor(dto.valor());
        novaVenda.setDataVenda(dto.data());

        // 4. Atualizar o status e o dono do cachorro
        cachorro.setStatus(StatusCachorro.VENDIDO);
        cachorro.setTutor(tutorDaVenda);

        // 5. Salvar a venda (as alterações no cachorro serão salvas pela transação)
        return vendaRepository.save(novaVenda);
    }
}