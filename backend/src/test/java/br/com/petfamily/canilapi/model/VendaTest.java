package br.com.petfamily.canilapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class VendaTest {

    private Venda venda;
    private Cachorro cachorroVendido;
    private Tutor novoTutor;
    private LocalDate dataVenda;

    @BeforeEach
    void setUp() {
        // Arrange: Prepara os objetos base para os testes
        cachorroVendido = new Cachorro();
        cachorroVendido.setId(1L);

        novoTutor = new Tutor("Beto Silva", "beto@email.com", "222");
        novoTutor.setId(2L);

        dataVenda = LocalDate.of(2024, 7, 29);

        venda = new Venda(1500.00, dataVenda, cachorroVendido, novoTutor);
        venda.setId(10L);
    }

    @Test
    @DisplayName("Deve criar uma venda e verificar seus atributos via getters")
    void constructorAndGetters_ShouldSetValuesCorrectly() {
        // Assert
        assertEquals(10L, venda.getId());
        assertEquals(1500.00, venda.getValorVenda());
        assertEquals(dataVenda, venda.getDataVenda());
        assertEquals(cachorroVendido, venda.getCachorroVendido());
        assertEquals(novoTutor, venda.getNovoTutor());
    }

    @Test
    @DisplayName("Deve permitir a alteração dos atributos via setters")
    void setters_ShouldUpdateObjectState() {
        // Arrange
        Cachorro outroCachorro = new Cachorro();
        outroCachorro.setId(99L);
        Tutor outroTutor = new Tutor();
        outroTutor.setId(98L);
        LocalDate outraData = LocalDate.now();

        // Act
        venda.setValorVenda(2000.50);
        venda.setDataVenda(outraData);
        venda.setCachorroVendido(outroCachorro);
        venda.setNovoTutor(outroTutor);

        // Assert
        assertEquals(2000.50, venda.getValorVenda());
        assertEquals(outraData, venda.getDataVenda());
        assertEquals(outroCachorro, venda.getCachorroVendido());
        assertEquals(outroTutor, venda.getNovoTutor());
    }

    @Test
    @DisplayName("Deve formatar a string de saída corretamente usando o método toString")
    void toString_ShouldReturnFormattedString() {
        // Arrange
        String expectedString = "Vendido para Beto Silva por R$ 1500.00 em 2024-07-29";

        // Act
        String actualString = venda.toString();

        // Assert (replace para tornar o teste independente de localidade)
        assertEquals(expectedString, actualString.replace(',', '.'));
    }

    @Test
    @DisplayName("Deve considerar duas vendas iguais se tiverem o mesmo ID")
    void equals_WithSameId_ShouldReturnTrue() {
        // Arrange
        Venda venda1 = new Venda(1000.0, LocalDate.now(), cachorroVendido, novoTutor);
        venda1.setId(123L);

        Venda venda2 = new Venda(2000.0, LocalDate.now(), new Cachorro(), new Tutor());
        venda2.setId(123L);

        // Assert
        assertTrue(venda1.equals(venda2));
        assertEquals(venda1.hashCode(), venda2.hashCode());
    }

    @Test
    @DisplayName("Deve considerar duas vendas diferentes se tiverem IDs diferentes")
    void equals_WithDifferentId_ShouldReturnFalse() {
        // Arrange
        Venda venda1 = new Venda(1000.0, LocalDate.now(), cachorroVendido, novoTutor);
        venda1.setId(123L);

        Venda venda2 = new Venda(1000.0, LocalDate.now(), cachorroVendido, novoTutor);
        venda2.setId(456L);

        // Assert
        assertFalse(venda1.equals(venda2));
        assertNotEquals(venda1.hashCode(), venda2.hashCode());
    }
}