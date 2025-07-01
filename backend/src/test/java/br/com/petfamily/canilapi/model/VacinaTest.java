package br.com.petfamily.canilapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class VacinaTest {

    private Vacina vacina;
    private Cachorro cachorro;
    private LocalDate dataAplicacao;
    private LocalDate proximaAplicacao;

    @BeforeEach
    void setUp() {
        // Arrange: Prepara os objetos base para os testes
        cachorro = new Cachorro();
        cachorro.setId(1L);
        cachorro.setNome("Rex");

        dataAplicacao = LocalDate.of(2023, 5, 15);
        proximaAplicacao = LocalDate.of(2024, 5, 15);

        // Cria uma nova instância de Vacina antes de cada teste
        vacina = new Vacina("V10 (Polivalente)", dataAplicacao, proximaAplicacao, 150.00);
        vacina.setId(50L);
        vacina.setCachorro(cachorro);
    }

    @Test
    @DisplayName("Deve criar uma vacina e verificar seus atributos via getters")
    void constructorAndGetters_ShouldSetValuesCorrectly() {
        // Assert: Verifica se os valores iniciais estão corretos
        assertEquals(50L, vacina.getId());
        assertEquals("V10 (Polivalente)", vacina.getNome());
        assertEquals(dataAplicacao, vacina.getDataAplicacao());
        assertEquals(proximaAplicacao, vacina.getDataProximaAplicacao());
        assertEquals(150.00, vacina.getValor());
        assertEquals(cachorro, vacina.getCachorro());
    }

    @Test
    @DisplayName("Deve permitir a alteração dos atributos via setters")
    void setters_ShouldUpdateObjectState() {
        // Arrange
        LocalDate novaDataAplicacao = LocalDate.now();
        LocalDate novaProximaAplicacao = LocalDate.now().plusYears(1);
        Cachorro outroCachorro = new Cachorro();
        outroCachorro.setId(2L);

        // Act
        vacina.setNome("Antirrábica");
        vacina.setDataAplicacao(novaDataAplicacao);
        vacina.setDataProximaAplicacao(novaProximaAplicacao);
        vacina.setValor(80.50);
        vacina.setCachorro(outroCachorro);

        // Assert
        assertEquals("Antirrábica", vacina.getNome());
        assertEquals(novaDataAplicacao, vacina.getDataAplicacao());
        assertEquals(novaProximaAplicacao, vacina.getDataProximaAplicacao());
        assertEquals(80.50, vacina.getValor());
        assertEquals(outroCachorro, vacina.getCachorro());
    }

    @Test
    @DisplayName("Deve lidar corretamente com próxima aplicação nula")
    void constructor_WithNullNextApplicationDate_ShouldBeAllowed() {
        // Act
        Vacina vacinaSemProxima = new Vacina("Dose Única", LocalDate.now(), null, 95.00);

        // Assert
        assertNotNull(vacinaSemProxima);
        assertNull(vacinaSemProxima.getDataProximaAplicacao());
    }

    @Test
    @DisplayName("Deve considerar duas vacinas iguais se tiverem o mesmo ID")
    void equals_WithSameId_ShouldReturnTrue() {
        // Arrange
        Vacina vacina1 = new Vacina("V10", dataAplicacao, proximaAplicacao, 150.0);
        vacina1.setId(99L);

        Vacina vacina2 = new Vacina("Raiva", LocalDate.now(), null, 80.0);
        vacina2.setId(99L);

        // Assert
        assertTrue(vacina1.equals(vacina2));
        assertEquals(vacina1.hashCode(), vacina2.hashCode());
    }

    @Test
    @DisplayName("Deve considerar duas vacinas diferentes se tiverem IDs diferentes")
    void equals_WithDifferentId_ShouldReturnFalse() {
        // Arrange
        Vacina vacina1 = new Vacina("V10", dataAplicacao, proximaAplicacao, 150.0);
        vacina1.setId(99L);

        Vacina vacina2 = new Vacina("V10", dataAplicacao, proximaAplicacao, 150.0);
        vacina2.setId(100L);

        // Assert
        assertFalse(vacina1.equals(vacina2));
        assertNotEquals(vacina1.hashCode(), vacina2.hashCode());
    }
}