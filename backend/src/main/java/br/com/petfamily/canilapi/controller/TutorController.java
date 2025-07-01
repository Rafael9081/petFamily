package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.TutorResponseDTO;
import br.com.petfamily.canilapi.model.Tutor;
import br.com.petfamily.canilapi.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tutores") // Todas as requisições para /tutores cairão neste controller
@CrossOrigin(origins = "*")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    @PostMapping
    public ResponseEntity<Tutor> criar(@RequestBody Tutor tutor, UriComponentsBuilder uriBuilder) {
        Tutor novoTutor = tutorService.criar(tutor);

        // Boa prática: Retornar o status 201 Created e a URL do novo recurso no header 'Location'
        URI uri = uriBuilder.path("/tutores/{id}").buildAndExpand(novoTutor.getId()).toUri();
        return ResponseEntity.created(uri).body(novoTutor);
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
    public ResponseEntity<Tutor> atualizar(@PathVariable Long id, @RequestBody Tutor dadosTutor) {
        Tutor tutorAtualizado = tutorService.atualizar(id, dadosTutor);
        return ResponseEntity.ok(tutorAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tutorService.deletar(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content, indicando sucesso sem corpo de resposta
    }
}