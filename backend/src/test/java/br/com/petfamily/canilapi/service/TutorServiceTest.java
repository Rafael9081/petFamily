package br.com.petfamily.canilapi.service;

import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.repository.TutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
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

    private Tutor tutor;

    @BeforeEach
    void setUp() {
        tutor = new Tutor("João da Silva", "joao@email.com", "11999998888");
        tutor.setId(1L);
    }

    @Test
    @DisplayName("Deve criar um tutor com sucesso quando os dados são válidos")
    void criar_ComDadosValidos_DeveRetornarTutorSalvo() {
        // Arrange
        when(tutorRepository.save(any(Tutor.class))).thenReturn(tutor);

        // Act
        Tutor tutorSalvo = tutorService.criar(tutor);

        // Assert
        assertNotNull(tutorSalvo);
        assertEquals("João da Silva", tutorSalvo.getNome());
        verify(tutorRepository).save(tutor);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar tutor com nome vazio")
    void criar_ComNomeVazio_DeveLancarIllegalArgumentException() {
        // Arrange
        tutor.setNome(" ");

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            tutorService.criar(tutor);
        });

        assertEquals("O nome do tutor não pode ser vazio.", exception.getMessage());
        verify(tutorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar um tutor que não possui cachorros")
    void deletar_TutorSemCachorros_DeveChamarDelete() {
        // Arrange
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        // Garante que a lista de cachorros está vazia
        tutor.setCachorros(Collections.emptyList());

        // Act
        tutorService.deletar(1L);

        // Assert
        verify(tutorRepository).delete(tutor);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar um tutor com cachorros associados")
    void deletar_TutorComCachorros_DeveLancarIllegalStateException() {
        // Arrange
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        // Simula que o tutor tem um cachorro
        tutor.adicionarCachorro(new Cachorro());

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

    @Test
    @DisplayName("Deve atualizar os dados de um tutor existente")
    void atualizar_ComDadosValidos_DeveRetornarTutorAtualizado() {
        // Arrange
        // Prepara os novos dados para a atualização
        Tutor dadosAtualizacao = new Tutor();
        dadosAtualizacao.setNome("João da Silva Santos");
        dadosAtualizacao.setEmail("joao.santos@novoemail.com");

        // Configura o mock para encontrar o tutor existente
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        // Configura o mock para retornar o tutor que for salvo
        when(tutorRepository.save(any(Tutor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tutor tutorAtualizado = tutorService.atualizar(1L, dadosAtualizacao);

        // Assert
        assertNotNull(tutorAtualizado);
        assertEquals(1L, tutorAtualizado.getId()); // ID não deve mudar
        assertEquals("João da Silva Santos", tutorAtualizado.getNome()); // Nome deve ser atualizado
        assertEquals("joao.santos@novoemail.com", tutorAtualizado.getEmail()); // Email deve ser atualizado
        assertEquals("11999998888", tutorAtualizado.getTelefone()); // Telefone (não fornecido na atualização) deve permanecer o mesmo

        verify(tutorRepository).findById(1L);
        verify(tutorRepository).save(tutor);
    }
}