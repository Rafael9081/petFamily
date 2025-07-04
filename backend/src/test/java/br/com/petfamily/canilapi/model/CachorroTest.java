package br.com.petfamily.canilapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CachorroTest {

    private Cachorro cachorroMacho;
    private Cachorro cachorroFemea;
    private Tutor tutorInicial;

    @BeforeEach
    void setUp() {
        tutorInicial = new Tutor("João Silva", "joao@email.com", "11999998888");
        tutorInicial.setId(1L);

        cachorroMacho = new Cachorro("Rex", Sexo.MACHO, LocalDate.of(2022, 1, 1), "Labrador", tutorInicial);
        cachorroFemea = new Cachorro("Bella", Sexo.FEMEA, LocalDate.of(2021, 5, 10), "Golden Retriever", tutorInicial);
    }

    @Test
    @DisplayName("Deve adicionar uma vacina à carteira do cachorro")
    void adicionarVacina_DeveIncluirVacinaNaLista() {
        // Arrange
        Vacina vacina = new Vacina("V10", LocalDate.now(), null, 120.0);
        int tamanhoInicialDaLista = cachorroMacho.getCarteiraVacinacao().size();

        // Act
        cachorroMacho.adicionarVacina(vacina);

        // Assert
        assertEquals(tamanhoInicialDaLista + 1, cachorroMacho.getCarteiraVacinacao().size());
        assertTrue(cachorroMacho.getCarteiraVacinacao().contains(vacina));
        assertEquals(cachorroMacho, vacina.getCachorro(), "A referência do cachorro na vacina deve ser definida.");
    }

    @Test
    @DisplayName("Deve adicionar uma ninhada para uma fêmea com sucesso")
    void adicionarNinhada_QuandoCachorroForFemea_DeveAdicionarComSucesso() {
        // Arrange
        Ninhada ninhada = new Ninhada(LocalDate.now(), 2, 3, cachorroFemea, cachorroMacho);

        // Act
        cachorroFemea.adicionarNinhada(ninhada);

        // Assert
        assertTrue(cachorroFemea.getHistoricoNinhadas().contains(ninhada));
        assertEquals(cachorroFemea, ninhada.getMae(), "A referência da mãe na ninhada deve ser definida.");
    }


    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar ninhada para um macho")
    void adicionarNinhada_QuandoCachorroForMacho_DeveLancarIllegalArgumentException() {
        // Arrange
        // CORREO: Crie uma ninhada válida usando os objetos de teste existentes.
        Ninhada ninhada = new Ninhada(LocalDate.now(), 1, 1, cachorroFemea, cachorroMacho);

        // Act & Assert
        // Agora, a exceção será lançada pelo método que você realmente quer testar.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cachorroMacho.adicionarNinhada(ninhada);
        });

        // E a mensagem da exceção será a que você espera.
        assertEquals("Apenas fêmeas podem ter ninhadas.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve calcular o custo total das despesas corretamente")
    void calcularCustoTotal_ComMultiplasDespesas_DeveRetornarSomaCorreta() {
        // Arrange
        cachorroMacho.adicionarDespesa(new Despesa("Ração Premium", 150.50, LocalDate.now(), null));
        cachorroMacho.adicionarDespesa(new Despesa("Consulta Veterinária", 200.00, LocalDate.now(), null));
        cachorroMacho.adicionarDespesa(new Despesa("Brinquedo", 49.50, LocalDate.now(), null));

        // Act
        double custoTotal = cachorroMacho.calcularCustoTotal();

        // Assert
        assertEquals(400.00, custoTotal, 0.01); // O terceiro parâmetro é uma margem de erro para doubles
    }

    @Test
    @DisplayName("Deve retornar zero para o custo total quando não há despesas")
    void calcularCustoTotal_SemDespesas_DeveRetornarZero() {
        // Act
        double custoTotal = cachorroMacho.calcularCustoTotal();

        // Assert
        assertEquals(0.0, custoTotal);
    }

    @Test
    @DisplayName("Deve realizar a venda de um cachorro que ainda não foi vendido")
    void realizarVenda_QuandoNaoVendido_DeveAtualizarStatusCorretamente() {
        // Arrange
        Tutor novoTutor = new Tutor("Maria Souza", "maria@email.com", "11777776666");
        novoTutor.setId(2L);
        Venda venda = new Venda(2500.0, LocalDate.now(), cachorroMacho, novoTutor);

        // Act
        cachorroMacho.realizarVenda(venda);

        // Assert
        assertTrue(cachorroMacho.isFoiVendido());
        assertEquals(novoTutor, cachorroMacho.getTutor());
        assertEquals(venda, cachorroMacho.getRegistroVenda());
        assertEquals(cachorroMacho, venda.getCachorro());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar vender um cachorro que já foi vendido")
    void realizarVenda_QuandoJaVendido_DeveLancarIllegalStateException() {
        // Arrange
        // Primeira venda
        Tutor primeiroComprador = new Tutor("Maria", "m@m.com", "123");
        Venda primeiraVenda = new Venda(2000.0, LocalDate.now(), cachorroMacho, primeiroComprador);
        cachorroMacho.realizarVenda(primeiraVenda);

        // Tentativa de segunda venda
        Tutor segundoComprador = new Tutor("Ana", "a@a.com", "456");
        Venda segundaVenda = new Venda(2200.0, LocalDate.now(), cachorroMacho, segundoComprador);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            cachorroMacho.realizarVenda(segundaVenda);
        });

        assertEquals("Este cachorro já foi vendido.", exception.getMessage());
    }
}