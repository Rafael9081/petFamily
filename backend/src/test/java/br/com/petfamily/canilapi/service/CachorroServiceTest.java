package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.CachorroRequestDTO;
import br.com.petfamily.canilapi.model.*;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Habilita a integração do Mockito com o JUnit 5
@ExtendWith(MockitoExtension.class)
class CachorroServiceTest {

    @Mock // Cria um "dublê" do repositório. Ele não acessará o banco de dados.
    private CachorroRepository cachorroRepository;

    @Mock // Cria um dublê para as outras dependências
    private TutorRepository tutorRepository;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks // Cria uma instância real do serviço e injeta os mocks acima nele.
    private CachorroService cachorroService;

    @Test
    @DisplayName("Deve buscar um cachorro por ID com sucesso")
    void buscarPorId_QuandoCachorroExiste_DeveRetornarCachorro() {
        // Arrange (Preparação)
        long idExistente = 1L;
        Cachorro cachorroMock = new Cachorro();
        cachorroMock.setId(idExistente);
        cachorroMock.setNome("Rex");

        // Configura o mock: "Quando o método findById for chamado com 1L, retorne o nosso cachorroMock"
        when(cachorroRepository.findById(idExistente)).thenReturn(Optional.of(cachorroMock));

        // Act (Ação)
        Cachorro cachorroEncontrado = cachorroService.buscarPorId(idExistente);

        // Assert (Verificação)
        assertNotNull(cachorroEncontrado);
        assertEquals("Rex", cachorroEncontrado.getNome());
        verify(cachorroRepository, times(1)).findById(idExistente); // Garante que o método foi chamado
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar um cachorro com ID inexistente")
    void buscarPorId_QuandoCachorroNaoExiste_DeveLancarEntityNotFoundException() {
        // Arrange
        long idInexistente = 99L;
        // Configura o mock: "Quando findById for chamado com 99L, retorne um Optional vazio"
        when(cachorroRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        // Verifica se a chamada ao método lança a exceção esperada
        assertThrows(EntityNotFoundException.class, () -> {
            cachorroService.buscarPorId(idInexistente);
        });
    }

    @Test
    @DisplayName("Deve criar um cachorro com sucesso")
    void criar_ComDadosValidos_DeveRetornarCachorroSalvo() {
        // Arrange
        Tutor tutor = new Tutor();
        tutor.setId(1L);
        CachorroRequestDTO dto = new CachorroRequestDTO("Rex", null, "Vira-lata", null, tutor.getId());

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(cachorroRepository.save(any(Cachorro.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Cachorro cachorroCriado = cachorroService.criar(dto);

        // Assert
        assertNotNull(cachorroCriado);
        assertEquals("Rex", cachorroCriado.getNome());
        assertEquals(tutor, cachorroCriado.getTutor());
        verify(cachorroRepository).save(any(Cachorro.class));
    }

    @Test
    @DisplayName("Deve gerar relatório financeiro para cachorro vendido com lucro")
    void gerarRelatorioFinanceiro_ParaCachorroVendido_DeveCalcularLucro() {
        // Arrange
        Cachorro cachorro = new Cachorro();
        cachorro.setId(1L);
        cachorro.setNome("Fido");

        // Adiciona despesas
        cachorro.adicionarDespesa(new Despesa("Ração", 100.0, LocalDate.now(), CategoriaDespesa.ALIMENTACAO));
        cachorro.adicionarDespesa(new Despesa("Vacina", 50.0, LocalDate.now(), CategoriaDespesa.SAUDE));

        // Simula a venda
        Venda venda = new Venda(500.0, LocalDate.now(), cachorro, new Tutor());
        cachorro.realizarVenda(venda);

        when(cachorroRepository.findById(1L)).thenReturn(Optional.of(cachorro));

        // Act
        br.com.petfamily.canilapi.dto.RelatorioFinanceiroDTO relatorio = cachorroService.gerarRelatorioFinanceiro(1L);

        // Assert
        assertNotNull(relatorio);
        assertEquals(150.0, relatorio.getCustoTotal());
        assertTrue(relatorio.isFoiVendido());
        assertNotNull(relatorio.getRegistroVenda());
        assertEquals(500.0, relatorio.getRegistroVenda().valor());
        assertEquals(350.0, relatorio.getLucro()); // 500 (venda) - 150 (custo)
    }

    @Test
    @DisplayName("Deve gerar relatório financeiro para cachorro não vendido")
    void gerarRelatorioFinanceiro_ParaCachorroNaoVendido_NaoDeveTerLucro() {
        // Arrange
        Cachorro cachorro = new Cachorro();
        cachorro.setId(1L);
        cachorro.adicionarDespesa(new Despesa("Brinquedo", 30.0, LocalDate.now(), CategoriaDespesa.OUTROS));

        when(cachorroRepository.findById(1L)).thenReturn(Optional.of(cachorro));

        // Act
        br.com.petfamily.canilapi.dto.RelatorioFinanceiroDTO relatorio = cachorroService.gerarRelatorioFinanceiro(1L);

        // Assert
        assertNotNull(relatorio);
        assertEquals(30.0, relatorio.getCustoTotal());
        assertFalse(relatorio.isFoiVendido());
        assertNull(relatorio.getRegistroVenda());
        assertNull(relatorio.getLucro());
    }
}