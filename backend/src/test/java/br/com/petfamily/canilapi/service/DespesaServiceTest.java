package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.DespesaRequestDTO;
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Despesa;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.DespesaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DespesaServiceTest {

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private CachorroRepository cachorroRepository;

    @InjectMocks
    private DespesaService despesaService;

    @Test
    @DisplayName("Deve registrar uma despesa com sucesso para um cachorro existente")
    void registrarDespesa_ComCachorroExistente_DeveRetornarDespesaSalva() {
        // Arrange
        long cachorroId = 1L;
        DespesaRequestDTO requestDTO = new DespesaRequestDTO("Ração Premium", 150.0, LocalDate.now());
        Cachorro mockCachorro = new Cachorro();
        mockCachorro.setId(cachorroId);

        when(cachorroRepository.findById(cachorroId)).thenReturn(Optional.of(mockCachorro));
        // Simula o save, retornando o objeto que foi passado para ele
        when(despesaRepository.save(any(Despesa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Despesa despesaRegistrada = despesaService.registrarDespesa(cachorroId, requestDTO);

        // Assert
        assertNotNull(despesaRegistrada);
        assertEquals("Ração Premium", despesaRegistrada.getDescricao());
        assertEquals(150.0, despesaRegistrada.getValor());
        assertEquals(mockCachorro, despesaRegistrada.getCachorro());

        // Verifica se o método save foi chamado uma vez
        verify(despesaRepository, times(1)).save(any(Despesa.class));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao registrar despesa para cachorro inexistente")
    void registrarDespesa_ComCachorroInexistente_DeveLancarExcecao() {
        // Arrange
        long cachorroIdInexistente = 99L;
        DespesaRequestDTO requestDTO = new DespesaRequestDTO("Ração", 150.0, LocalDate.now());

        when(cachorroRepository.findById(cachorroIdInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            despesaService.registrarDespesa(cachorroIdInexistente, requestDTO);
        });

        // Garante que o save nunca foi chamado
        verify(despesaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar uma despesa existente com sucesso")
    void deletarDespesa_ComIdExistente_DeveChamarDelete() {
        // Arrange
        long despesaId = 1L;
        Despesa mockDespesa = new Despesa();
        when(despesaRepository.findById(despesaId)).thenReturn(Optional.of(mockDespesa));

        // Act
        despesaService.deletarDespesa(despesaId);

        // Assert
        // Captura o argumento para garantir que o objeto correto foi deletado
        ArgumentCaptor<Despesa> despesaCaptor = ArgumentCaptor.forClass(Despesa.class);
        verify(despesaRepository, times(1)).delete(despesaCaptor.capture());
        assertEquals(mockDespesa, despesaCaptor.getValue());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar deletar despesa inexistente")
    void deletarDespesa_ComIdInexistente_DeveLancarExcecao() {
        // Arrange
        long despesaIdInexistente = 99L;
        when(despesaRepository.findById(despesaIdInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            despesaService.deletarDespesa(despesaIdInexistente);
        });

        verify(despesaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve listar despesas de um cachorro existente")
    void listarDespesasDeUmCachorro_ComCachorroExistente_DeveRetornarLista() {
        // Arrange
        long cachorroId = 1L;
        when(cachorroRepository.existsById(cachorroId)).thenReturn(true);
        when(despesaRepository.findByCachorro_Id(cachorroId)).thenReturn(Collections.singletonList(new Despesa()));

        // Act
        List<Despesa> despesas = despesaService.listarDespesasDeUmCachorro(cachorroId);

        // Assert
        assertNotNull(despesas);
        assertFalse(despesas.isEmpty());
        verify(despesaRepository, times(1)).findByCachorro_Id(cachorroId);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao listar despesas de cachorro inexistente")
    void listarDespesasDeUmCachorro_ComCachorroInexistente_DeveLancarExcecao() {
        // Arrange
        long cachorroIdInexistente = 99L;
        when(cachorroRepository.existsById(cachorroIdInexistente)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            despesaService.listarDespesasDeUmCachorro(cachorroIdInexistente);
        });

        verify(despesaRepository, never()).findByCachorro_Id(anyLong());
    }

    @Test
    @DisplayName("Deve retornar 0.0 quando não houver despesas no período para o cálculo de custo total")
    void calcularCustoTotalPorPeriodo_QuandoNaoHaDespesas_DeveRetornarZero() {
        // Arrange
        LocalDate inicio = LocalDate.now().minusMonths(1);
        LocalDate fim = LocalDate.now();
        when(despesaRepository.sumDespesasByPeriodo(inicio, fim)).thenReturn(null);

        // Act
        Double total = despesaService.calcularCustoTotalPorPeriodo(inicio, fim);

        // Assert
        assertEquals(0.0, total);
    }
}