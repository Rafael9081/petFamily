package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.CachorroRequestDTO;
import br.com.petfamily.canilapi.controller.dto.DespesaRequestDTO;
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Sexo;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.repository.CachorroRepository;
import br.com.petfamily.canilapi.repository.TutorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // Configura o MockMvc para fazer requisições
@Transactional // Garante que cada teste rode em uma transação e seja desfeito no final
class CachorroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired // Injete o repositde cachorro para criar um para os testes
    private CachorroRepository cachorroRepository;

    private Tutor tutorSalvo;

    @BeforeEach
    void setUp() {
        cachorroRepository.deleteAll();
        tutorRepository.deleteAll();
        // Cria um tutor no banco de dados em memória antes de cada teste
        Tutor tutor = new Tutor();
        tutor.setNome("Rafael");
        tutor.setEmail("rafael@email.com");
        tutorSalvo = tutorRepository.save(tutor);
    }

    @Test
    @DisplayName("Deve criar um cachorro com sucesso e retornar 201 Created")
    void criar_ComDadosValidos_DeveRetornar201() throws Exception {
        // Arrange
        CachorroRequestDTO requestDTO = new CachorroRequestDTO(
                "Bolinha", Sexo.MACHO, "Poodle", LocalDate.now().minusYears(1), tutorSalvo.getId()
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);

        // Act & Assert
        mockMvc.perform(post("/cachorros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated()) // Verifica o status HTTP
                .andExpect(header().exists("Location")) // Verifica se o header Location foi retornado
                .andExpect(jsonPath("$.id").exists()) // Verifica se o corpo da resposta tem um ID
                .andExpect(jsonPath("$.nome").value("Bolinha")); // Verifica o nome no corpo
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar criar um cachorro com nome em branco")
    void criar_ComNomeEmBranco_DeveRetornar400() throws Exception {
        // Arrange
        CachorroRequestDTO requestDTO = new CachorroRequestDTO(
                "", Sexo.MACHO, "Poodle", LocalDate.now(), tutorSalvo.getId()
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);

        // Act & Assert
        mockMvc.perform(post("/cachorros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest()); // Verifica se a validação funcionou
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar um cachorro com ID inexistente")
    void buscarPorId_ComIdInexistente_DeveRetornar404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/cachorros/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve buscar um cachorro por ID com sucesso e retornar 200 OK")
    void buscarPorId_ComIdExistente_DeveRetornar200() throws Exception {
        // Arrange: Cria um cachorro no banco de dados para o teste
        Cachorro cachorro = new Cachorro("Fido", Sexo.MACHO, LocalDate.now(),"Golden Reatreiver", tutorSalvo);
        Cachorro cachorroSalvo = cachorroRepository.save(cachorro);
        Long idExistente = cachorroSalvo.getId();

        // Act & Assert
        mockMvc.perform(get("/cachorros/{id}", idExistente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idExistente))
                .andExpect(jsonPath("$.nome").value("Fido"));
    }

    @Test
    @DisplayName("Deve deletar um cachorro com sucesso e retornar 204 No Content")
    void deletar_ComIdExistente_DeveRetornar204() throws Exception {
        // Arrange
        Cachorro cachorro = new Cachorro("Toby", Sexo.MACHO, LocalDate.now(),"Beagle", tutorSalvo);
        Cachorro cachorroSalvo = cachorroRepository.save(cachorro);
        Long idExistente = cachorroSalvo.getId();

        // Act & Assert
        mockMvc.perform(delete("/cachorros/{id}", idExistente))
                .andExpect(status().isNoContent()); // Verifica o status 204

        // Opcional, mas recomendado: Verifica se o recurso realmente foi deletado
        mockMvc.perform(get("/cachorros/{id}", idExistente))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar deletar um cachorro com ID inexistente")
    void deletar_ComIdInexistente_DeveRetornar404() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/cachorros/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um cachorro com sucesso e retornar 200 OK")
    void atualizar_ComDadosValidos_DeveRetornar200() throws Exception {
        // Arrange: Primeiro, crie um cachorro no banco para poder atualizá-lo.
        Tutor tutor = tutorRepository.save(new Tutor("Tutor Original", "original@email.com"));
        Cachorro cachorroOriginal = new Cachorro("Rex", Sexo.MACHO, LocalDate.now(), "Vira-lata", tutor);
        cachorroRepository.save(cachorroOriginal);

        // Crie o DTO com os novos dados.
        CachorroRequestDTO requestDTO = new CachorroRequestDTO("Rex Atualizado", Sexo.MACHO,  "Poodle", LocalDate.now(), tutor.getId());
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);

        // Act & Assert
        mockMvc.perform(put("/cachorros/{id}", cachorroOriginal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Rex Atualizado"))
                .andExpect(jsonPath("$.raca").value("Poodle"));
    }

    @Test
    @DisplayName("Deve atualizar parcialmente um cachorro (apenas o nome) e retornar 200 OK")
    void atualizarParcial_ApenasComNome_DeveRetornar200() throws Exception {
        // Arrange
        Tutor tutor = tutorRepository.save(new Tutor("Tutor Original", "original@email.com"));
        Cachorro cachorroOriginal = new Cachorro("Bolinha", Sexo.MACHO, LocalDate.now(), "SRD", tutor);
        cachorroRepository.save(cachorroOriginal);

        // O Map representa o JSON que será enviado, contendo apenas os campos a serem alterados.
        Map<String, Object> camposParaAtualizar = Map.of("nome", "Bolinha II");
        String jsonRequest = objectMapper.writeValueAsString(camposParaAtualizar);

        // Act & Assert
        mockMvc.perform(patch("/cachorros/{id}", cachorroOriginal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Bolinha II"))
                .andExpect(jsonPath("$.raca").value("SRD")); // Verifica que a raça não foi alterada
    }

    @Test
    @DisplayName("Deve adicionar uma despesa a um cachorro existente e retornar 201 Created")
    void adicionarDespesa_ComCachorroExistente_DeveRetornar201() throws Exception {
        // Arrange
        Cachorro cachorro = cachorroRepository.save(new Cachorro("Fido", Sexo.MACHO, LocalDate.now(), "Golden", null));
        DespesaRequestDTO despesaDTO = new DespesaRequestDTO("Ração Super Premium", 250.0, LocalDate.now());
        String jsonRequest = objectMapper.writeValueAsString(despesaDTO);

        // Act & Assert
        mockMvc.perform(post("/cachorros/{id}/despesas", cachorro.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }
}