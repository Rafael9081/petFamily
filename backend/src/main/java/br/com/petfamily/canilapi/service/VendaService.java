package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.VendaRequestFlexDTO;
import br.com.petfamily.canilapi.controller.dto.VendaResponseDTO;
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
    private final TutorService tutorService;

    public VendaService(VendaRepository vendaRepository, CachorroRepository cachorroRepository, TutorRepository tutorRepository, TutorService tutorService) {
        this.vendaRepository = vendaRepository;
        this.cachorroRepository = cachorroRepository;
        this.tutorRepository = tutorRepository;
        this.tutorService = tutorService;
    }

    /**
     * Realiza a venda de um cachorro, permitindo associar a um tutor existente ou criar um novo.
     * Retorna um DTO seguro para evitar LazyInitializationException.
     *
     * @param cachorroId O ID do cachorro a ser vendido.
     * @param dto        Os dados da venda, incluindo valor, data e informações do tutor.
     * @return Um VendaResponseDTO com os detalhes da venda concluída.
     */
    @Transactional
    public VendaResponseDTO realizarVendaFlex(Long cachorroId, VendaRequestFlexDTO dto) {
        // 1. Validações iniciais
        Cachorro cachorro = cachorroRepository.findById(cachorroId)
                .orElseThrow(() -> new EntityNotFoundException("Cachorro não encontrado com ID: " + cachorroId));

        if (cachorro.getStatus() == StatusCachorro.VENDIDO) {
            throw new IllegalStateException("Este cachorro já foi vendido.");
        }

        Tutor tutorDaVenda;

        // 2. Lógica "Flex" para obter o tutor
        if (dto.tutorId() != null) {
            tutorDaVenda = tutorRepository.findById(dto.tutorId())
                    .orElseThrow(() -> new EntityNotFoundException("Tutor existente não encontrado com ID: " + dto.tutorId()));
        } else {
            tutorDaVenda = tutorService.criar(dto.novoTutor());
        }

        // 3. Preparar a venda
        Venda novaVenda = new Venda();
        novaVenda.setValor(dto.valor());
        novaVenda.setDataVenda(dto.data());
        novaVenda.setNovoTutor(tutorDaVenda);

        // 4. ENCAPSULAMENTO: Delega as mudanças de estado para a própria entidade Cachorro.
        cachorro.realizarVenda(novaVenda);

        // 5. Persistir a nova venda no banco de dados.
        Venda vendaSalva = vendaRepository.save(novaVenda);

        // 6. ROBUSTEZ: Busca a entidade novamente com todos os detalhes para construir o DTO.
        // O movoTutor`
        // na mesma query, prevenindo a LazyInitializationException de forma performática.
        Venda vendaCompleta = vendaRepository.findByIdWithDetails(vendaSalva.getId())
                .orElseThrow(() -> new IllegalStateException("Erro crítico: Venda recém-criada não encontrada."));

        // 7. Retornar o DTO seguro, construído dentro da transação.
        return new VendaResponseDTO(vendaCompleta);
    }
}