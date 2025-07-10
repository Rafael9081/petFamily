package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.*;
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Despesa;
import br.com.petfamily.canilapi.service.CachorroService;
import br.com.petfamily.canilapi.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import br.com.petfamily.canilapi.model.Venda;


@RestController
@RequestMapping("/cachorros")
@CrossOrigin(origins = "*")
public class CachorroController {

    // 1. Injeção de dependência via construtor (melhor prática)
    private final CachorroService cachorroService;
    private final VendaService vendaService;

    public CachorroController(CachorroService cachorroService, VendaService vendaService) {
        this.cachorroService = cachorroService;
        this.vendaService = vendaService;
    }

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<CachorroResponseDTO>> listarPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Page<Cachorro> cachorros = cachorroService.listarTodosPaginado(page, size);
        org.springframework.data.domain.Page<CachorroResponseDTO> dtos = cachorros.map(CachorroResponseDTO::new);
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
    public ResponseEntity<Void> adicionarDespesa(@PathVariable Long id, @RequestBody @Valid DespesaRequestDTO requestDTO,
                                                 UriComponentsBuilder uriBuilder) {
        // Aqui você poderia retornar a despesa criada
        Despesa despesa = cachorroService.adicionarDespesa(id, requestDTO);
        URI uri = uriBuilder.path("/cachorros/{cachorroId}/despesas/{despesaId}")
                .buildAndExpand(id, despesa.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CachorroResponseDTO> atualizar(@PathVariable Long id,
                                                         @RequestBody @Valid CachorroRequestDTO dto) {
        Cachorro cachorroAtualizado = cachorroService.atualizar(id, dto);
        return ResponseEntity.ok(new CachorroResponseDTO(cachorroAtualizado));
    }

    @GetMapping("/{id}/relatorio-financeiro")
    public ResponseEntity<br.com.petfamily.canilapi.dto.RelatorioFinanceiroDTO> getRelatorioFinanceiro(@PathVariable Long id) {
        br.com.petfamily.canilapi.dto.RelatorioFinanceiroDTO relatorio = cachorroService.gerarRelatorioFinanceiro(id);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<CachorroResponseDTO>> listarTodos() {
        List<Cachorro> cachorros = cachorroService.listarTodos();
        List<CachorroResponseDTO> dtos = cachorros.stream()
                .map(CachorroResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CachorroResponseDTO> atualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos) {
        Cachorro cachorro = cachorroService.atualizarParcial(id, campos);
        return ResponseEntity.ok(new CachorroResponseDTO(cachorro));
    }

    @PostMapping("/{id}/vender")
    public ResponseEntity<VendaResponseDTO> vender(@PathVariable Long id, @RequestBody @Valid VendaRequestDTO dto) {
       Venda vendaRealizada = vendaService.realizarVenda(id, dto);
        return ResponseEntity.ok(new VendaResponseDTO(vendaRealizada));
    }




}