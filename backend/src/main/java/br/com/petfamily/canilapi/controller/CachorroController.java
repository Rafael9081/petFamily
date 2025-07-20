package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.*;
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Despesa;
import br.com.petfamily.canilapi.model.Ninhada;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.service.CachorroService;
import br.com.petfamily.canilapi.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cachorros")
public class CachorroController {

    // Injeção de dependência via construtor (ótima prática!)
    private final CachorroService cachorroService;
    private final VendaService vendaService;

    public CachorroController(CachorroService cachorroService, VendaService vendaService) {
        this.cachorroService = cachorroService;
        this.vendaService = vendaService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<CachorroResponseDTO>> listarPaginados(Pageable pageable) {
        Page<CachorroResponseDTO> pageDeDtos = cachorroService.listarTodosPaginado(pageable);
        PaginatedResponseDTO<CachorroResponseDTO> respostaPaginada = new PaginatedResponseDTO<>(pageDeDtos);
        return ResponseEntity.ok(respostaPaginada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CachorroResponseDTO> buscarPorId(@PathVariable Long id) {
        CachorroResponseDTO cachorroDTO = cachorroService.buscarDTOPorId(id);
        return ResponseEntity.ok(cachorroDTO);
    }

    @PostMapping
    public ResponseEntity<CachorroResponseDTO> criarCachorro(@RequestBody @Valid CachorroPostRequestDTO dto) {
        Cachorro cachorroSalvo = cachorroService.criar(dto);
        CachorroResponseDTO responseDTO = new CachorroResponseDTO(cachorroSalvo);

        // Monta a URI de localização (ótima prática REST)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cachorroSalvo.getId())
                .toUri();

        // Retorna a resposta com status 201 Created e o DTO no corpo
        return ResponseEntity.created(location).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cachorroService.deletarCachorro(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/despesas")
    public ResponseEntity<Void> adicionarDespesa(@PathVariable Long id, @RequestBody @Valid DespesaRequestDTO requestDTO,
                                                 UriComponentsBuilder uriBuilder) {
        Despesa despesa = cachorroService.adicionarDespesa(id, requestDTO);
        URI uri = uriBuilder.path("/cachorros/{cachorroId}/despesas/{despesaId}")
                .buildAndExpand(id, despesa.getId()) // Este método será encontrado após o Rebuild
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CachorroResponseDTO> atualizar(@PathVariable Long id,
                                                         @RequestBody @Valid CachorroRequestDTO dto) {
        Cachorro cachorroAtualizado = cachorroService.atualizar(id, dto);
        return ResponseEntity.ok(new CachorroResponseDTO(cachorroAtualizado));
    }

    @GetMapping("/{id}/relatorio-financeiro")
    public ResponseEntity<RelatorioFinanceiroDTO> getRelatorioFinanceiro(@PathVariable Long id) {
        RelatorioFinanceiroDTO relatorio = cachorroService.gerarRelatorioFinanceiro(id);
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
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
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

    @GetMapping("/{maeId}/ninhadas")
    public ResponseEntity<List<NinhadaResponseDTO>> listarNinhadasDaMae(@PathVariable Long maeId) {
        List<Ninhada> ninhadas = cachorroService.listarNinhadasDaMae(maeId);
        List<NinhadaResponseDTO> dtos = ninhadas.stream()
                .map(NinhadaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}