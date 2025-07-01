package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Ninhada;
import br.com.petfamily.canilapi.model.Sexo;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.NinhadaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita o uso do Mockito com JUnit 5
class NinhadaServiceTest {

    @Mock // Cria um objeto "fake" (mock) do repositório. Não vamos usar o banco de dados real.
    private NinhadaRepository ninhadaRepository;

    @Mock // Cria um mock do repositório de cachorros
    private CachorroRepository cachorroRepository;

    @InjectMocks // Cria uma instância real do serviço e injeta os mocks acima nele
    private NinhadaService ninhadaService;

    private Cachorro mae;
    private Cachorro pai;

    @BeforeEach // Este método roda antes de cada teste, preparando os dados
    void setUp() {
        mae = new Cachorro();
        mae.setId(1L);
        mae.setNome("Bella");
        mae.setSexo(Sexo.FEMEA);

        pai = new Cachorro();
        pai.setId(2L);
        pai.setNome("Max");
        pai.setSexo(Sexo.MACHO);
    }

    @Test
    @DisplayName("Deve registrar uma ninhada com sucesso quando os dados são válidos")
    void registraNinhada_ComDadosValidos_DeveRetornarNinhada() {
        // Arrange (Organizar/Cenário)
        // Dizemos ao mock o que fazer quando for chamado.
        // "Quando findById(1L) for chamado, retorne a nossa mãe de teste"
        when(cachorroRepository.findById(1L)).thenReturn(Optional.of(mae));
        // "Quando findById(2L) for chamado, retorne o nosso pai de teste"
        when(cachorroRepository.findById(2L)).thenReturn(Optional.of(pai));

        LocalDate dataNascimento = LocalDate.now();
        int machos = 2;
        int femeas = 3;

        // Act (Ação)
        // Chamamos o método que queremos testar
        Ninhada ninhadaRegistrada = ninhadaService.registraNinhada(1L, 2L, dataNascimento, machos, femeas);

        // Assert (Verificação)
        // Verificamos se o resultado é o esperado
        assertNotNull(ninhadaRegistrada);
        assertEquals(mae, ninhadaRegistrada.getMae());
        assertEquals(pai, ninhadaRegistrada.getPai());
        assertEquals(5, ninhadaRegistrada.getQuantidadeTotalFilhotes());

        // Verifica se o método save foi chamado na mãe para salvar a ninhada
        verify(cachorroRepository, times(1)).save(mae);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar ninhada com mãe do sexo Macho")
    void registraNinhada_QuandoMaeNaoForFemea_DeveLancarIllegalArgumentException() {
        // Arrange
        mae.setSexo(Sexo.MACHO); // Alteramos o cenário para forçar o erro
        when(cachorroRepository.findById(1L)).thenReturn(Optional.of(mae));
        when(cachorroRepository.findById(2L)).thenReturn(Optional.of(pai));

        // Act & Assert
        // Verificamos se a exceção correta é lançada
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ninhadaService.registraNinhada(1L, 2L, LocalDate.now(), 1, 1);
        });

        // Verificamos a mensagem da exceção
        assertEquals("O cachorro mãe deve ser do sexo Fêmea.", exception.getMessage());

        // Garante que o método save nunca foi chamado, pois a operação falhou antes
        verify(cachorroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a mãe não for encontrada")
    void registraNinhada_QuandoMaeNaoEncontrada_DeveLancarRuntimeException() {
        // Arrange
        when(cachorroRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ninhadaService.registraNinhada(1L, 2L, LocalDate.now(), 1, 1);
        });

        // CORREÇÃO: A mensagem de erro no seu serviço deve ser algo como isto.
        // O original "Mncontrada..." estava com um typo.
        assertEquals("Mãe não encontrada com o ID: 1", exception.getMessage());
    }
}