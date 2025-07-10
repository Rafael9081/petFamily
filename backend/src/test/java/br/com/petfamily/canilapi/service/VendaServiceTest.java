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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;
    @Mock
    private CachorroRepository cachorroRepository;
    @Mock
    private TutorRepository tutorRepository;

    @InjectMocks
    private VendaService vendaService;

    @Test
    @DisplayName("Deve realizar uma venda com sucesso quando os dados são válidos")
    void realizarVenda_ComDadosValidos_DeveRetornarVendaComSucesso() {
        // Arrange (Preparação)
        Long cachorroId = 1L;
        Long novoTutorId = 2L;
        VendaRequestDTO requestDTO = new VendaRequestDTO(novoTutorId, 2500.0, LocalDate.now());

        Cachorro cachorro = new Cachorro(); // Usamos um objeto real para testar a mudança de estado
        cachorro.setId(cachorroId);
        cachorro.setFoiVendido(false);

        Tutor novoTutor = new Tutor();
        novoTutor.setId(novoTutorId);

        // Configura o comportamento dos mocks
        when(cachorroRepository.findByIdWithAssociations(cachorroId)).thenReturn(Optional.of(cachorro));
        when(tutorRepository.findById(novoTutorId)).thenReturn(Optional.of(novoTutor));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Ação)
        Venda vendaRealizada = vendaService.realizarVenda(cachorroId, requestDTO);

        // Assert (Verificação)
        assertNotNull(vendaRealizada);
        assertEquals(2500.0, vendaRealizada.getValor());
        assertEquals(cachorro, vendaRealizada.getCachorro());
        assertEquals(novoTutor, vendaRealizada.getNovoTutor());

        // Verifica se o estado do cachorro foi alterado corretamente
        assertTrue(cachorro.isFoiVendido());
        assertEquals(novoTutor, cachorro.getTutor());
        assertNotNull(cachorro.getRegistroVenda());

        // Verifica se a operação de salvar foi chamada
        verify(vendaRepository).save(any(Venda.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao tentar vender um cachorro já vendido")
    void realizarVenda_QuandoCachorroJaVendido_DeveLancarIllegalStateException() {
        // Arrange
        Long cachorroId = 1L;
        Long novoTutorId = 2L;
        VendaRequestDTO requestDTO = new VendaRequestDTO(novoTutorId, 2500.0, LocalDate.now());

        Cachorro cachorro = new Cachorro();
        cachorro.setId(cachorroId);
        cachorro.setFoiVendido(true); // Estado crucial para este teste

        when(cachorroRepository.findByIdWithAssociations(cachorroId)).thenReturn(Optional.of(cachorro));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            vendaService.realizarVenda(cachorroId, requestDTO);
        });

        assertEquals("Este cachorro já foi vendido e não pode ser vendido novamente.", exception.getMessage());
        verify(vendaRepository, never()).save(any());
        verify(tutorRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando o cachorro não existe")
    void realizarVenda_QuandoCachorroNaoEncontrado_DeveLancarEntityNotFoundException() {
        // Arrange
        Long cachorroIdInexistente = 99L;
        VendaRequestDTO requestDTO = new VendaRequestDTO(2L, 2500.0, LocalDate.now());

        when(cachorroRepository.findByIdWithAssociations(cachorroIdInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            vendaService.realizarVenda(cachorroIdInexistente, requestDTO);
        });
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando o tutor não existe")
    void realizarVenda_QuandoTutorNaoEncontrado_DeveLancarEntityNotFoundException() {
        // Arrange
        Long cachorroId = 1L;
        Long tutorIdInexistente = 98L;
        VendaRequestDTO requestDTO = new VendaRequestDTO(tutorIdInexistente, 2500.0, LocalDate.now());

        when(cachorroRepository.findByIdWithAssociations(cachorroId)).thenReturn(Optional.of(new Cachorro()));
        when(tutorRepository.findById(tutorIdInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            vendaService.realizarVenda(cachorroId, requestDTO);
        });
    }
}