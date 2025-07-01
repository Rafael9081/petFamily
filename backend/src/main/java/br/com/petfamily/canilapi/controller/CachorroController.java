package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.CachorroRequestDTO;
import br.com.petfamily.canilapi.controller.dto.CachorroResponseDTO;
import br.com.petfamily.canilapi.controller.dto.DespesaRequestDTO;
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.service.CachorroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cachorros")
@CrossOrigin(origins = "*")
public class CachorroController {

    // 1. Injeção de dependência via construtor (melhor prática)
    private final CachorroService cachorroService;

    public CachorroController(CachorroService cachorroService) {
        this.cachorroService = cachorroService;
    }

    @GetMapping
    public ResponseEntity<List<CachorroResponseDTO>> listarTodos() {
        // 2. CORREÇÃO: Busca a lista de entidades do serviço
        List<Cachorro> cachorros = cachorroService.listarTodos();
        // 3. Converte a lista de Entidades para uma lista de DTOs
        List<CachorroResponseDTO> dtos = cachorros.stream()
                .map(CachorroResponseDTO::new) // Equivalente a .map(cachorro -> new CachorroResponseDTO(cachorro))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CachorroResponseDTO> buscarPorId(@PathVariable Long id) {
        // 4. CORREÇÃO: Busca a entidade nica do serviço
        Cachorro cachorro = cachorroService.buscarPorId(id);
        // 5. Converte a Entidade para o DTO de resposta
        return ResponseEntity.ok(new CachorroResponseDTO(cachorro));
    }

    @PostMapping
    public ResponseEntity<CachorroResponseDTO> criar(@RequestBody @Valid CachorroRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        Cachorro novoCachorro = cachorroService.criar(requestDTO);
        URI uri = uriBuilder.path("/cachorros/{id}").buildAndExpand(novoCachorro.getId()).toUri();
        return ResponseEntity.created(uri).body(new CachorroResponseDTO(novoCachorro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cachorroService.deletarCachorro(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/despesas")
    public ResponseEntity<Void> adicionarDespesa(@PathVariable Long id, @RequestBody @Valid DespesaRequestDTO requestDTO) {
        cachorroService.adicionarDespesa(id, requestDTO);
        // Retornar 201 Created é mais apropriado para criação de sub-recursos
        return ResponseEntity.created(null).build();
    }
}