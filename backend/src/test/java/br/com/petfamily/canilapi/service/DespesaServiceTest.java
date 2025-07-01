package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Despesa;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.DespesaRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DespesaServiceTest {

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private CachorroRepository cachorroRepository;

    @InjectMocks
    private DespesaService despesaService;

    private Cachorro cachorro;
    private Despesa despesa;

    @BeforeEach
    void setUp() {
        cachorro = new Cachorro();
        cachorro.setId(1L);
        cachorro.setNome("Rex");

        despesa = new Despesa("Ração", 150.0, LocalDate.now(), null);
    }

    @Test
    @DisplayName("Deve registrar uma despesa com sucesso para um cachorro existente")
    void registrarDespesa_ComDadosValidos_DeveRetornarDespesaSalva() {
        // Arrange
        when(cachorroRepository.findById(1L)).thenReturn(Optional.of(cachorro));
        when(despesaRepository.save(any(Despesa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Despesa despesaSalva = despesaService.registrarDespesa(1L, despesa);

        // Assert
        assertNotNull(despesaSalva);
        assertEquals(cachorro, despesaSalva.getCachorro());
        assertEquals("Ração", despesaSalva.getDescricao());

        // Captura o argumento para verificar se o cachorro foi associado corretamente
        ArgumentCaptor<Despesa> despesaCaptor = ArgumentCaptor.forClass(Despesa.class);
        verify(despesaRepository).save(despesaCaptor.capture());
        assertEquals(cachorro, despesaCaptor.getValue().getCachorro());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar despesa para cachorro inexistente")
    void registrarDespesa_ParaCachorroInexistente_DeveLancarEntityNotFoundException() {
        // Arrange
        when(cachorroRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(EntityNotFoundException.class, () -> {
            despesaService.registrarDespesa(99L, despesa);
        });

        assertEquals("Cachorro não encontrado com o ID: 99", exception.getMessage());
        verify(despesaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar despesa com valor zero")
    void registrarDespesa_ComValorZero_DeveLancarIllegalArgumentException() {
        // Arrange
        despesa.setValor(0.0);
        when(cachorroRepository.findById(1L)).thenReturn(Optional.of(cachorro));

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            despesaService.registrarDespesa(1L, despesa);
        });

        assertEquals("O valor da despesa deve ser maior que zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar uma despesa existente com sucesso")
    void deletarDespesa_ComIdExistente_DeveChamarDeleteById() {
        // Arrange
        when(despesaRepository.existsById(10L)).thenReturn(true);

        // Act
        despesaService.deletarDespesa(10L);

        // Assert
        verify(despesaRepository, times(1)).deleteById(10L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar despesa inexistente")
    void deletarDespesa_ComIdInexistente_DeveLancarEntityNotFoundException() {
        // Arrange
        when(despesaRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        var exception = assertThrows(EntityNotFoundException.class, () -> {
            despesaService.deletarDespesa(99L);
        });

        assertEquals("Despesa não encontrada com o ID: 99", exception.getMessage());
        verify(despesaRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar despesa com descrição vazia")
    void registrarDespesa_ComDescricaoVazia_DeveLancarIllegalArgumentException() {
        // Arrange
        despesa.setDescricao("   "); // Descrição com espaços em branco
        when(cachorroRepository.findById(1L)).thenReturn(Optional.of(cachorro));

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            despesaService.registrarDespesa(1L, despesa);
        });

        assertEquals("A descrição da despesa não pode ser vazia.", exception.getMessage());
        verify(despesaRepository, never()).save(any()); // Garante que não tentou salvar
    }
}