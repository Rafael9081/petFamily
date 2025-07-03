package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.model.Cachorro;
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
}