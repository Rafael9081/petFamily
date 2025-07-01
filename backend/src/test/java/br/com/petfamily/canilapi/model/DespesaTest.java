package br.com.petfamily.canilapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DespesaTest {

    private Despesa despesa;
    private Cachorro cachorroAssociado;
    private LocalDate dataDaDespesa;

    @BeforeEach
    void setUp() {
        // Arrange: Prepara os objetos necessários para cada teste
        cachorroAssociado = new Cachorro();
        cachorroAssociado.setId(1L);
        cachorroAssociado.setNome("Rex");

        dataDaDespesa = LocalDate.of(2023, 10, 26);

        // Cria uma nova instância de Despesa antes de cada teste
        despesa = new Despesa("Ração Super Premium 15kg", 250.75, dataDaDespesa, CategoriaDespesa.ALIMENTACAO);
        despesa.setId(100L);
        despesa.setCachorro(cachorroAssociado);
    }

    @Test
    @DisplayName("Deve criar uma despesa e verificar seus atributos via getters")
    void constructorAndGetters_ShouldSetValuesCorrectly() {
        // Assert: Verifica se os valores iniciais estão corretos
        assertEquals(100L, despesa.getId());
        assertEquals("Ração Super Premium 15kg", despesa.getDescricao());
        assertEquals(250.75, despesa.getValor());
        assertEquals(dataDaDespesa, despesa.getData());
        assertEquals(CategoriaDespesa.ALIMENTACAO, despesa.getCategoria());
        assertEquals(cachorroAssociado, despesa.getCachorro());
    }

    @Test
    @DisplayName("Deve permitir a alteração dos atributos via setters")
    void setters_ShouldUpdateObjectState() {
        // Arrange: Prepara os novos dados
        LocalDate novaData = LocalDate.of(2024, 1, 15);
        Cachorro outroCachorro = new Cachorro();
        outroCachorro.setId(2L);

        // Act: Modifica o estado do objeto usando os setters
        despesa.setDescricao("Consulta de rotina");
        despesa.setValor(180.00);
        despesa.setData(novaData);
        despesa.setCategoria(CategoriaDespesa.VETERINARIO);
        despesa.setCachorro(outroCachorro);

        // Assert: Verifica se o estado foi atualizado corretamente
        assertEquals("Consulta de rotina", despesa.getDescricao());
        assertEquals(180.00, despesa.getValor());
        assertEquals(novaData, despesa.getData());
        assertEquals(CategoriaDespesa.VETERINARIO, despesa.getCategoria());
        assertEquals(outroCachorro, despesa.getCachorro());
    }

    @Test
    @DisplayName("Deve formatar a string de saída corretamente usando o método toString")
    void toString_ShouldReturnFormattedString() {
        // Arrange: Define a string esperada
        // O formato de data padrão de LocalDate.toString() é "yyyy-MM-dd"
        // O formato de double pode usar vírgula ou ponto dependendo do Locale da máquina.
        // O teste abaixo lida com ambos os casos.
        String expectedString = "2023-10-26 [ALIMENTACAO] - R$ 250.75 (Ração Super Premium 15kg)";

        // Act: Chama o método a ser testado
        String actualString = despesa.toString();

        // Assert: Compara o resultado, tornando o teste independente de localidade (ponto vs. vírgula)
        assertEquals(expectedString, actualString.replace(',', '.'));
    }
}