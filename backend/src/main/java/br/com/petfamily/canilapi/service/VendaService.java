package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import br.com.petfamily.canilapi.repository.VendaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class VendaService {
    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private CachorroRepository cachorroRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Transactional
    public Venda registrarVenda(Long cachorroId, Long novoTutorId, double valorVenda) {
        Cachorro cachorro = cachorroRepository.findById(cachorroId)
                .orElseThrow(() -> new EntityNotFoundException("Cachorro não encontrado com o ID: " + cachorroId));

        Tutor novoTutor = tutorRepository.findById(novoTutorId)
                .orElseThrow(() -> new EntityNotFoundException("Tutor (comprador) não encontrado com o ID: " + novoTutorId));


        if (cachorro.isFoiVendido()) {
            throw new IllegalStateException("Este cachorro já foi vendido e não pode ser vendido novamente.");
        }

        Venda novaVenda = new Venda(valorVenda, LocalDate.now(), cachorro, novoTutor);
        cachorro.realizarVenda(novaVenda);
        cachorroRepository.save(cachorro);

        return novaVenda;
    }

    public Venda buscarPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada com o ID: " + id));
    }

    public List<Venda> listarTodas() {
        return vendaRepository.findAll();
    }

    public List<Venda> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data de início não pode ser posterior à data de fim.");
        }
        return vendaRepository.findAllByDataVendaBetween(dataInicio, dataFim);
    }
}

