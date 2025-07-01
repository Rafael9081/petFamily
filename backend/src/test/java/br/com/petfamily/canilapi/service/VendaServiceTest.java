package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private CachorroRepository cachorroRepository;

    @Mock
    private TutorRepository tutorRepository;

    // Não precisamos mockar o VendaRepository para o teste de registrarVenda,
    // pois a persistência ocorre via CachorroRepository.

    @InjectMocks
    private VendaService vendaService;

    private Cachorro cachorroDisponivel;
    private Tutor comprador;

    @BeforeEach
    void setUp() {
        cachorroDisponivel = new Cachorro();
        cachorroDisponivel.setId(1L);
        cachorroDisponivel.setNome("Rex");
        cachorroDisponivel.setFoiVendido(false); // Garante que o cachorro está disponível

        comprador = new Tutor("Maria", "maria@email.com", "111");
        comprador.setId(2L);
    }

    @Test
    @DisplayName("Deve registrar uma venda com sucesso e atualizar o status do cachorro")
    void registrarVenda_ComDadosValidos_DeveRetornarVendaEAtualizarCachorro() {
        // Arrange
        when(cachorroRepository.findById(1L)).thenReturn(Optional.of(cachorroDisponivel));
        when(tutorRepository.findById(2L)).thenReturn(Optional.of(comprador));

        // Act
        Venda vendaRealizada = vendaService.registrarVenda(1L, 2L, 2000.0);

        // Assert
        assertNotNull(vendaRealizada);
        assertEquals(2000.0, vendaRealizada.getValorVenda());
        assertEquals(comprador, vendaRealizada.getNovoTutor());
        assertEquals(cachorroDisponivel, vendaRealizada.getCachorroVendido());

        // Captura o cachorro que foi salvo para verificar seu estado final
        ArgumentCaptor<Cachorro> cachorroCaptor = ArgumentCaptor.forClass(Cachorro.class);
        verify(cachorroRepository).save(cachorroCaptor.capture());

        Cachorro cachorroSalvo = cachorroCaptor.getValue();
        assertTrue(cachorroSalvo.isFoiVendido());
        assertEquals(comprador, cachorroSalvo.getTutor());
        assertEquals(vendaRealizada, cachorroSalvo.getRegistroVenda());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar vender um cachorro que já foi vendido")
    void registrarVenda_ParaCachorroJaVendido_DeveLancarIllegalStateException() {
        // Arrange
        cachorroDisponivel.setFoiVendido(true); // Simula que o cachorro já foi vendido
        when(cachorroRepository.findById(1L)).thenReturn(Optional.of(cachorroDisponivel));
        when(tutorRepository.findById(2L)).thenReturn(Optional.of(comprador));

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, () -> {
            vendaService.registrarVenda(1L, 2L, 2000.0);
        });

        assertEquals("Este cachorro já foi vendido e não pode ser vendido novamente.", exception.getMessage());
        verify(cachorroRepository, never()).save(any()); // Garante que nada foi salvo
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar vender um cachorro inexistente")
    void registrarVenda_ComCachorroInexistente_DeveLancarEntityNotFoundException() {
        // Arrange
        when(cachorroRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(EntityNotFoundException.class, () -> {
            vendaService.registrarVenda(99L, 2L, 2000.0);
        });

        assertEquals("Cachorro não encontrado com o ID: 99", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o comprador (tutor) não for encontrado")
    void registrarVenda_ComTutorInexistente_DeveLancarEntityNotFoundException() {
        // Arrange
        when(cachorroRepository.findById(1L)).thenReturn(Optional.of(cachorroDisponivel));
        when(tutorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(EntityNotFoundException.class, () -> {
            vendaService.registrarVenda(1L, 99L, 2000.0);
        });

        assertEquals("Tutor (comprador) não encontrado com o ID: 99", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar por período com data de início posterior à data de fim")
    void listarPorPeriodo_ComDataInicioInvalida_DeveLancarIllegalArgumentException() {
        // Arrange
        LocalDate dataInicio = LocalDate.of(2024, 1, 10);
        LocalDate dataFim = LocalDate.of(2024, 1, 1); // Data de fim é anterior

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            vendaService.listarPorPeriodo(dataInicio, dataFim);
        });

        assertEquals("A data de início não pode ser posterior à data de fim.", exception.getMessage());
    }
}