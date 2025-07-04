package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.VendaRequestDTO;
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import br.com.petfamily.canilapi.repository.VendaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock // Cria um "dublê" para o repositório de Venda
    private VendaRepository vendaRepository;

    @Mock // Cria um "dublê" para o repositório de Cachorro
    private CachorroRepository cachorroRepository;

    @Mock // Cria um "dublê" para o repositório de Tutor
    private TutorRepository tutorRepository;

    @InjectMocks // Cria uma instância real do VendaService e injeta os mocks acima
    private VendaService vendaService;

    @Test
    @DisplayName("Deve realizar uma venda com sucesso quando os dados são válidos")
    void realizarVenda_ComDadosValidos_DeveRetornarVendaComSucesso() {
        // Arrange (Preparação)
        long cachorroId = 1L;
        long tutorId = 2L;
        VendaRequestDTO requestDTO = new VendaRequestDTO(cachorroId, tutorId, 2500.0);

        // Usamos mock() para poder verificar chamadas de método na entidade
        Cachorro mockCachorro = mock(Cachorro.class);
        Tutor mockTutor = new Tutor();

        // Configura o comportamento dos mocks
        when(cachorroRepository.findById(cachorroId)).thenReturn(Optional.of(mockCachorro));
        when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(mockTutor));
        when(mockCachorro.isFoiVendido()).thenReturn(false); // Garante que o cachorro não foi vendido
        // Simula o save, retornando o objeto que foi passado para ele
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Ação)
        Venda vendaRealizada = vendaService.realizarVenda(requestDTO);

        // Assert (Verificação)
        assertNotNull(vendaRealizada);
        assertEquals(2500.0, vendaRealizada.getValor());
        assertEquals(mockCachorro, vendaRealizada.getCachorro());
        assertEquals(mockTutor, vendaRealizada.getNovoTutor());

        // Verifica se os métodos corretos foram chamados (interações)
        verify(mockCachorro).realizarVenda(any(Venda.class));
        verify(vendaRepository).save(any(Venda.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao tentar vender um cachorro já vendido")
    void realizarVenda_QuandoCachorroJaVendido_DeveLancarIllegalStateException() {
        // Arrange
        long cachorroId = 1L;
        VendaRequestDTO requestDTO = new VendaRequestDTO(cachorroId, 2L, 2500.0);

        Cachorro mockCachorro = new Cachorro();
        // Definimos o estado crucial para este teste
        mockCachorro.setFoiVendido(true);

        when(cachorroRepository.findById(cachorroId)).thenReturn(Optional.of(mockCachorro));

        // Act & Assert
        // Verificamos se a exceção correta é lançada
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            vendaService.realizarVenda(requestDTO);
        });

        assertEquals("Este cachorro já foi vendido e não pode ser vendido novamente.", exception.getMessage());

        // Garante que a operação de salvar nunca foi chamada, pois a transação falhou antes
        verify(vendaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando o cachorro não existe")
    void realizarVenda_QuandoCachorroNaoEncontrado_DeveLancarEntityNotFoundException() {
        // Arrange
        long cachorroIdInexistente = 99L;
        VendaRequestDTO requestDTO = new VendaRequestDTO(cachorroIdInexistente, 2L, 2500.0);

        // Simulamos que o repositório não encontrou o cachorro
        when(cachorroRepository.findById(cachorroIdInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            vendaService.realizarVenda(requestDTO);
        });
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando o tutor não existe")
    void realizarVenda_QuandoTutorNaoEncontrado_DeveLancarEntityNotFoundException() {
        // Arrange
        long tutorIdInexistente = 98L;
        VendaRequestDTO requestDTO = new VendaRequestDTO(1L, tutorIdInexistente, 2500.0);

        // Simulamos que o cachorro existe, mas o tutor não
        when(cachorroRepository.findById(1L)).thenReturn(Optional.of(new Cachorro()));
        when(tutorRepository.findById(tutorIdInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            vendaService.realizarVenda(requestDTO);
        });
    }
}