package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.TutorRequestDTO;
import br.com.petfamily.canilapi.controller.dto.TutorResponseDTO;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tutores") // Todas as requisições para /tutores cairão neste controller
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping
    public ResponseEntity<TutorResponseDTO> criar(@RequestBody @Valid TutorRequestDTO dto, UriComponentsBuilder uriBuilder) {
        Tutor novoTutor = tutorService.criar(dto);

        // Boa prática: Retornar o status 201 Created e a URL do novo recurso no header 'Location'
        URI uri = uriBuilder.path("/tutores/{id}").buildAndExpand(novoTutor.getId()).toUri();
        return ResponseEntity.created(uri).body(new TutorResponseDTO(novoTutor));
    }

    @GetMapping
    public ResponseEntity<List<TutorResponseDTO>> listarTodos() {
        List<TutorResponseDTO> tutores = tutorService.listarTodosDTO();
        return ResponseEntity.ok(tutores); // Retorna 200 OK com a lista de tutores
    }

    @GetMapping("/{id}")
    public ResponseEntity<TutorResponseDTO> buscarPorId(@PathVariable Long id) {
        TutorResponseDTO tutor = tutorService.buscarPorIdDTO(id);
        return ResponseEntity.ok(tutor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TutorResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid TutorRequestDTO dto) {
        Tutor tutorAtualizado = tutorService.atualizar(id, dto);
        return ResponseEntity.ok(new TutorResponseDTO(tutorAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tutorService.deletar(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content, indicando sucesso sem corpo de resposta
    }
}