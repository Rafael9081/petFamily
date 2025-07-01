package br.com.petfamily.canilapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class NinhadaTest {

    private Cachorro mae;
    private Cachorro pai;
    private LocalDate dataNascimento;

    @BeforeEach
    void setUp() {
        // Arrange: Prepara os objetos base para os testes
        mae = new Cachorro();
        mae.setId(1L);
        mae.setSexo(Sexo.FEMEA);

        pai = new Cachorro();
        pai.setId(2L);
        pai.setSexo(Sexo.MACHO);

        dataNascimento = LocalDate.of(2024, 1, 1);
    }

    @Test
    @DisplayName("Deve criar uma ninhada com sucesso quando todos os dados são válidos")
    void constructor_WithValidData_ShouldCreateInstance() {
        // Act
        Ninhada ninhada = new Ninhada(dataNascimento, 3, 2, mae, pai);
        ninhada.setId(10L);

        // Assert
        assertNotNull(ninhada);
        assertEquals(10L, ninhada.getId());
        assertEquals(dataNascimento, ninhada.getDataNascimento());
        assertEquals(3, ninhada.getQuantidadeMachos());
        assertEquals(2, ninhada.getQuantidadeFemeas());
        assertEquals(mae, ninhada.getMae());
        assertEquals(pai, ninhada.getPai());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a data de nascimento for nula")
    void constructor_WithNullBirthDate_ShouldThrowException() {
        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            new Ninhada(null, 3, 2, mae, pai);
        });
        assertEquals("Data de nascimento, mãe e pai não podem ser nulos.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a mãe for nula")
    void constructor_WithNullMother_ShouldThrowException() {
        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            new Ninhada(dataNascimento, 3, 2, null, pai);
        });
        assertEquals("Data de nascimento, mãe e pai não podem ser nulos.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a quantidade de machos for negativa")
    void constructor_WithNegativeMales_ShouldThrowException() {
        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            new Ninhada(dataNascimento, -1, 2, mae, pai);
        });
        assertEquals("A quantidade de filhotes não pode ser negativa.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve calcular a quantidade total de filhotes corretamente")
    void getQuantidadeTotalFilhotes_ShouldReturnCorrectSum() {
        // Arrange
        Ninhada ninhada = new Ninhada(dataNascimento, 4, 5, mae, pai);

        // Act
        int total = ninhada.getQuantidadeTotalFilhotes();

        // Assert
        assertEquals(9, total);
    }

    @Test
    @DisplayName("Deve retornar zero como total de filhotes quando não há filhotes")
    void getQuantidadeTotalFilhotes_WithZeroPuppies_ShouldReturnZero() {
        // Arrange
        Ninhada ninhada = new Ninhada(dataNascimento, 0, 0, mae, pai);

        // Act
        int total = ninhada.getQuantidadeTotalFilhotes();

        // Assert
        assertEquals(0, total);
    }

    @Test
    @DisplayName("Deve considerar duas ninhadas iguais se tiverem o mesmo ID")
    void equals_WithSameId_ShouldReturnTrue() {
        // Arrange
        Ninhada ninhada1 = new Ninhada(dataNascimento, 1, 1, mae, pai);
        ninhada1.setId(123L);

        Ninhada ninhada2 = new Ninhada(dataNascimento, 2, 2, mae, pai);
        ninhada2.setId(123L);

        // Assert
        assertTrue(ninhada1.equals(ninhada2));
        assertEquals(ninhada1.hashCode(), ninhada2.hashCode());
    }

    @Test
    @DisplayName("Deve considerar duas ninhadas diferentes se tiverem IDs diferentes")
    void equals_WithDifferentId_ShouldReturnFalse() {
        // Arrange
        Ninhada ninhada1 = new Ninhada(dataNascimento, 1, 1, mae, pai);
        ninhada1.setId(123L);

        Ninhada ninhada2 = new Ninhada(dataNascimento, 1, 1, mae, pai);
        ninhada2.setId(456L);

        // Assert
        assertFalse(ninhada1.equals(ninhada2));
        assertNotEquals(ninhada1.hashCode(), ninhada2.hashCode());
    }
}