package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.CachorroRequestDTO;
import br.com.petfamily.canilapi.controller.dto.DespesaRequestDTO; // Importe o DTO de Despesa
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Despesa;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CachorroService {

    // 1. Dependências declaradas como 'final' para injeção via construtor
    private final CachorroRepository cachorroRepository;
    private final TutorRepository tutorRepository;

    // 2. Injeção de dependência via construtor (prática recomendada)
    public CachorroService(CachorroRepository cachorroRepository, TutorRepository tutorRepository) {
        this.cachorroRepository = cachorroRepository;
        this.tutorRepository = tutorRepository;
    }

    public Cachorro buscarPorId(Long id) {
        return cachorroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cachorro não encontrado com ID: " + id));
    }

    @Transactional
    public void deletarCachorro(Long id) {
        // Reutiliza a busca que já lança exceção se o cachorro não existir
        this.buscarPorId(id);
        cachorroRepository.deleteById(id);
    }

    public List<Cachorro> listarTodos() {
        return cachorroRepository.findAll();
    }

    @Transactional
    public Cachorro criar(CachorroRequestDTO dto) {
        Tutor tutor = tutorRepository.findById(dto.tutorId())
                .orElseThrow(() -> new EntityNotFoundException("Tutor não encontrado com ID: " + dto.tutorId()));

        Cachorro novoCachorro = new Cachorro();
        novoCachorro.setNome(dto.nome());
        novoCachorro.setRaca(dto.raca());
        novoCachorro.setDataNascimento(dto.dataNascimento());
        novoCachorro.setSexo(dto.sexo());
        novoCachorro.setTutor(tutor);

        // 3. Código comentado removido e chave '}' extra corrigida.
        return cachorroRepository.save(novoCachorro);
    }

    @Transactional
    // 4. Método agora usa DespesaRequestDTO, tornando a API consistente e segura.
    public void adicionarDespesa(Long cachorroId, DespesaRequestDTO dto) {
        Cachorro cachorro = buscarPorId(cachorroId);

        Despesa novaDespesa = new Despesa();
        novaDespesa.setDescricao(dto.descricao());
        novaDespesa.setValor(dto.valor());
        // Define a data atual se nenhuma for fornecida no DTO
        novaDespesa.setData(dto.data() != null ? dto.data() : LocalDate.now());

        cachorro.adicionarDespesa(novaDespesa);
        cachorroRepository.save(cachorro);
    }

    @Transactional
    public Venda realizarVenda(Long cachorroId, Long novoTutorId, double valor) {
        Cachorro cachorro = buscarPorId(cachorroId);
        Tutor novoTutor = tutorRepository.findById(novoTutorId)
                .orElseThrow(() -> new EntityNotFoundException("Tutor não encontrado com ID: " + novoTutorId));

        Venda novaVenda = new Venda(valor, LocalDate.now(), cachorro, novoTutor);
        cachorro.realizarVenda(novaVenda);

        cachorroRepository.save(cachorro);
        return novaVenda;
    }
}