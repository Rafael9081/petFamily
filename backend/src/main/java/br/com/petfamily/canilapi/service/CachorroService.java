package br.com.petfamily.canilapi.service;


import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Despesa;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CachorroService {

    @Autowired
    private CachorroRepository cachorroRepository;

    @Autowired
    private TutorRepository tutorRepository;

    public Cachorro salvarCachorro(Cachorro cachorro) {
        return cachorroRepository.save(cachorro);
    }

    public Cachorro buscarPorId(Long id) {
        return cachorroRepository.findById(id).orElse(null);
    }

    public void deletarCachorro(Long id) {
        cachorroRepository.deleteById(id);
    }

    public List<Cachorro> listarTodos() {
        return cachorroRepository.findAll();
    }

    public void adicionarDespesa(Long cachorroId, Despesa novaDespesa) {
        Cachorro cachorro = buscarPorId(cachorroId); // Reutiliza o método que já criamos
        cachorro.adicionarDespesa(novaDespesa);
        cachorroRepository.save(cachorro);
    }

    public Venda realizarVenda(Long cachorroId, Long novoTutorId, double valor) {
        Cachorro cachorro = buscarPorId(cachorroId);
        Tutor novoTutor = tutorRepository.findById(novoTutorId)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com ID: " + novoTutorId));
        Venda novaVenda = new Venda(valor, LocalDate.now(), cachorro, novoTutor);
        cachorro.realizarVenda(novaVenda);

        cachorroRepository.save(cachorro);
        return novaVenda;

    }

}
