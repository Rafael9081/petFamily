package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.TutorRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TutorRepository tutorRepository;

    @BeforeEach
    void setUp() {
        // Limpa o repositório para garantir a independência dos testes
        tutorRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um tutor com sucesso e retornar 201 Created")
    void criar_ComDadosValidos_DeveRetornar201() throws Exception {
        // Arrange
        TutorRequestDTO requestDTO = new TutorRequestDTO("Ana Zaira", "ana.zaira@email.com", "11987654321");
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);

        // Act & Assert
        mockMvc.perform(post("/tutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("Ana Zaira"))
                .andExpect(jsonPath("$.email").value("ana.zaira@email.com"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar criar tutor com email inválido")
    void criar_ComEmailInvalido_DeveRetornar400() throws Exception {
        // Arrange
        TutorRequestDTO requestDTO = new TutorRequestDTO("Ana Zaira", "email-invalido", "123456789");
        String jsonRequest = objectMapper.writeValueAsString(requestDTO);

        // Act & Assert
        mockMvc.perform(post("/tutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest()); // Verifica se a validação @Email funcionou
    }

    @Test
    @DisplayName("Deve buscar um tutor por ID e retornar 200 OK")
    void buscarPorId_ComIdExistente_DeveRetornar200() throws Exception {
        // Arrange: Salva um tutor no banco para poder buscá-lo
        Tutor tutorSalvo = tutorRepository.save(new Tutor("Carlos Pereira", "carlos@email.com"));

        // Act & Assert
        mockMvc.perform(get("/tutores/{id}", tutorSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tutorSalvo.getId()))
                .andExpect(jsonPath("$.nome").value("Carlos Pereira"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar tutor com ID inexistente")
    void buscarPorId_ComIdInexistente_DeveRetornar404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/tutores/999")) // ID que não existe
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um tutor e retornar 204 No Content")
    void deletar_ComIdExistente_DeveRetornar204() throws Exception {
        // Arrange
        Tutor tutorSalvo = tutorRepository.save(new Tutor("Tutor a ser deletado", "delete@email.com"));

        // Act & Assert
        mockMvc.perform(delete("/tutores/{id}", tutorSalvo.getId()))
                .andExpect(status().isNoContent());
    }
}