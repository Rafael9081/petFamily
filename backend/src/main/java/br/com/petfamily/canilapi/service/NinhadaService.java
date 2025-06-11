package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Ninhada;
import br.com.petfamily.canilapi.model.Sexo;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.NinhadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class NinhadaService {

    @Autowired
    private NinhadaRepository ninhadaRepository;

    @Autowired
    private CachorroRepository cachorroRepository;

    @Transactional
    public Ninhada registraNinhada(Long maeId, Long paiId, LocalDate dataNascimento, int machos, int femeas) {
        Cachorro mae = cachorroRepository.findById(maeId)
                .orElseThrow(() -> new RuntimeException("Mãe não encontrada com o ID: " + maeId));

        Cachorro pai = cachorroRepository.findById(paiId)
                .orElseThrow(() -> new RuntimeException("Pai não encontrado com o ID: " + paiId));

        if (mae.getSexo() != Sexo.FEMEA) {
            throw new IllegalArgumentException("O cachorro mãe deve ser do sexo Fêmea.");
        }

        if (pai.getSexo() != Sexo.MACHO) {
            throw new IllegalArgumentException("O cachorro pai deve ser do sexo Macho.");
        }

        if (mae.getId().equals(pai.getId())) {
            throw new IllegalArgumentException("O cachorro mãe e pai não podem ser o mesmo.");
        }

        Ninhada novaNinhada = new Ninhada(dataNascimento, machos, femeas, mae, pai);

        mae.adicionarNinhada(novaNinhada);
        cachorroRepository.save(mae);
        return novaNinhada;
    }

    public List<Ninhada> listarNinhadasDeUmaMae(Long maeId) {
        if (!cachorroRepository.existsById(maeId)) {
            throw new RuntimeException("Mãe não encontrada com o ID: " + maeId);
        }
        return ninhadaRepository.findByMae_Id(maeId);
    }

    public List<Ninhada> listarNinhadasPorAno(int ano) {
        return ninhadaRepository.findByAnoNascimento(ano);
    }


}
