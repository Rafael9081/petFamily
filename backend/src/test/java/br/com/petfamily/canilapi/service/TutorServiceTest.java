package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.controller.dto.TutorRequestDTO;
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.repository.TutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @InjectMocks
    private TutorService tutorService;

    @Test
    @DisplayName("Deve criar um tutor com sucesso quando os dados do DTO são válidos")
    void criar_ComDadosValidos_DeveRetornarTutorSalvo() {
        // Arrange
        // 1. CORREÇÃO: Criamos o DTO que o serviço espera receber.
        TutorRequestDTO requestDTO = new TutorRequestDTO("João da Silva", "joao@email.com");

        // Simula o que o repositório fará: salvar e retornar uma entidade com ID.
        when(tutorRepository.save(any(Tutor.class))).thenAnswer(invocation -> {
            Tutor tutorParaSalvar = invocation.getArgument(0);
            tutorParaSalvar.setId(1L); // Simula o ID gerado pelo banco
            return tutorParaSalvar;
        });

        // Act
        // 2. CORREÇÃO: Chamamos o método com o DTO.
        Tutor tutorSalvo = tutorService.criar(requestDTO);

        // Assert
        assertNotNull(tutorSalvo);
        assertEquals("João da Silva", tutorSalvo.getNome());
        assertEquals("joao@email.com", tutorSalvo.getEmail());

        // Captura o argumento para verificar se os dados do DTO foram mapeados corretamente para a entidade.
        ArgumentCaptor<Tutor> tutorCaptor = ArgumentCaptor.forClass(Tutor.class);
        verify(tutorRepository).save(tutorCaptor.capture());
        assertEquals("João da Silva", tutorCaptor.getValue().getNome());
    }

    @Test
    @DisplayName("Deve atualizar os dados de um tutor existente")
    void atualizar_ComDadosValidos_DeveRetornarTutorAtualizado() {
        // Arrange
        // 1. CORREÇÃO: Criamos o DTO com os dados da atualização.
        TutorRequestDTO requestDTO = new TutorRequestDTO("João da Silva Santos", "joao.santos@novoemail.com");

        // Prepara o tutor que já existe no "banco"
        Tutor tutorExistente = new Tutor();
        tutorExistente.setId(1L);
        tutorExistente.setNome("João Antigo");
        tutorExistente.setEmail("joao.antigo@email.com");

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutorExistente));

        // Act
        // 2. CORREÇÃO: Chamamos o método de atualização com o DTO.
        Tutor tutorAtualizado = tutorService.atualizar(1L, requestDTO);

        // Assert
        assertNotNull(tutorAtualizado);
        assertEquals(1L, tutorAtualizado.getId()); // ID não deve mudar
        assertEquals("João da Silva Santos", tutorAtualizado.getNome()); // Nome deve ser atualizado
        assertEquals("joao.santos@novoemail.com", tutorAtualizado.getEmail()); // Email deve ser atualizado
    }

    @Test
    @DisplayName("Deve deletar um tutor que não possui cachorros")
    void deletar_TutorSemCachorros_DeveChamarDelete() {
        // Arrange
        Tutor tutor = new Tutor();
        tutor.setId(1L);
        tutor.setCachorros(Collections.emptyList()); // Garante que a lista de cachorros está vazia

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));

        // Act
        tutorService.deletar(1L);

        // Assert
        verify(tutorRepository).delete(tutor);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar um tutor com cachorros associados")
    void deletar_TutorComCachorros_DeveLancarIllegalStateException() {
        // Arrange
        Tutor tutor = new Tutor();
        tutor.setId(1L);
        tutor.adicionarCachorro(new Cachorro()); // Simula que o tutor tem um cachorro

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, () -> {
            tutorService.deletar(1L);
        });

        assertEquals("Não é possível deletar um tutor que possui cachorros associados.", exception.getMessage());
        verify(tutorRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar um tutor inexistente")
    void deletar_TutorInexistente_DeveLancarEntityNotFoundException() {
        // Arrange
        when(tutorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            tutorService.deletar(99L);
        });
    }
}


