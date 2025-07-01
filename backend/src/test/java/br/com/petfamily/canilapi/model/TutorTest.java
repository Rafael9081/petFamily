package br.com.petfamily.canilapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TutorTest {

    private Tutor tutor;

    @BeforeEach
    void setUp() {
        // Arrange: Cria uma nova instância de Tutor antes de cada teste
        tutor = new Tutor("Carlos Pereira", "carlos.p@example.com", "11987654321");
        tutor.setId(1L);
    }

    @Test
    @DisplayName("Deve criar um tutor e verificar seus atributos via getters")
    void constructorAndGetters_ShouldSetValuesCorrectly() {
        // Assert: Verifica se os valores iniciais estão corretos
        assertEquals(1L, tutor.getId());
        assertEquals("Carlos Pereira", tutor.getNome());
        assertEquals("carlos.p@example.com", tutor.getEmail());
        assertEquals("11987654321", tutor.getTelefone());
        assertTrue(tutor.getCachorros().isEmpty(), "A lista de cachorros deve iniciar vazia.");
    }

    @Test
    @DisplayName("Deve permitir a alteração dos atributos via setters")
    void setters_ShouldUpdateObjectState() {
        // Arrange
        List<Cachorro> novaListaCachorros = new ArrayList<>();
        novaListaCachorros.add(new Cachorro());

        // Act: Modifica o estado do objeto usando os setters
        tutor.setNome("Carlos A. Pereira");
        tutor.setEmail("carlos.pereira@newdomain.com");
        tutor.setTelefone("21912345678");
        tutor.setCachorros(novaListaCachorros);

        // Assert: Verifica se o estado foi atualizado corretamente
        assertEquals("Carlos A. Pereira", tutor.getNome());
        assertEquals("carlos.pereira@newdomain.com", tutor.getEmail());
        assertEquals("21912345678", tutor.getTelefone());
        assertEquals(1, tutor.getCachorros().size());
    }

    @Test
    @DisplayName("Deve adicionar um cachorro à lista do tutor e definir a referência bidirecional")
    void adicionarCachorro_ShouldAddToListAndSetTutorOnCachorro() {
        // Arrange
        Cachorro novoCachorro = new Cachorro("Bolinha", Sexo.MACHO, LocalDate.now(), "Pug", null);
        assertTrue(tutor.getCachorros().isEmpty(), "Prcondição: a lista de cachorros deve estar vazia.");
        assertNull(novoCachorro.getTutor(), "Pré-condição: o novo cachorro não deve ter um tutor ainda.");

        // Act
        tutor.adicionarCachorro(novoCachorro);

        // Assert
        assertEquals(1, tutor.getCachorros().size(), "A lista de cachorros do tutor deve conter um item.");
        assertTrue(tutor.getCachorros().contains(novoCachorro), "A lista deve conter o cachorro adicionado.");
        assertEquals(tutor, novoCachorro.getTutor(), "O tutor do cachorro deve ser definido para manter a consistência da relação.");
    }
}