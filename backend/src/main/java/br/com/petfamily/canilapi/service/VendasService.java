package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import br.com.petfamily.canilapi.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class VendasService {
    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private CachorroRepository cachorroRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Transactional
    public Venda registrarVenda(Long cachorroId, Long novoTutorId, double valor) {
        Cachorro cachorro = cachorroRepository.findById(cachorroId)
                .orElseThrow(() -> new RuntimeException("Cachorro não encontrado com o ID: " + cachorroId));

        Tutor novoTutor = tutorRepository.findById(novoTutorId)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com o ID: " + novoTutorId));

        Venda novaVenda = new Venda(valor, LocalDate.now(), cachorro, novoTutor);

        cachorro.realizarVenda(novaVenda);
        cachorroRepository.save(cachorro);

        return novaVenda;
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

