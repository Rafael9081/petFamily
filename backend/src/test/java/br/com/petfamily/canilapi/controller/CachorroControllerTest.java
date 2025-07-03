package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.CachorroRequestDTO;
import br.com.petfamily.canilapi.model.Sexo;
import br.com.petfamily.canilapi.model.Tutor;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private Tutor tutorSalvo;

    @BeforeEach
    void setUp() {
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
}