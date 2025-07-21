package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.*;
import br.com.petfamily.canilapi.model.Cachorro;
import br.com.petfamily.canilapi.model.Despesa;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.service.CachorroService;
import br.com.petfamily.canilapi.service.NinhadaService;
import br.com.petfamily.canilapi.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cachorros")
public class CachorroController {

    private final CachorroService cachorroService;
    private final VendaService vendaService;
    private final NinhadaService ninhadaService;

    public CachorroController(CachorroService cachorroService, VendaService vendaService, NinhadaService ninhadaService) {
        this.cachorroService = cachorroService;
        this.vendaService = vendaService;
        this.ninhadaService = ninhadaService;
    }

    @GetMapping
    public ResponseEntity<Page<CachorroResponseDTO>> listarPaginados(Pageable pageable) {
        Page<CachorroResponseDTO> pageDeDtos = cachorroService.listarTodosPaginado(pageable);
        return ResponseEntity.ok(pageDeDtos);
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

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cachorroSalvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cachorroService.deletarCachorro(id);
        return ResponseEntity.noContent().build();
    }

    // --- ENDPOINT DE DESPESA MELHORADO ---
    @PostMapping("/{id}/despesas")
    public ResponseEntity<DespesaInfoDTO> adicionarDespesa(@PathVariable Long id, @RequestBody @Valid DespesaRequestDTO requestDTO) {
        // 1. O serviço já retorna a entidade Despesa completa e salva
        Despesa despesaCriada = cachorroService.adicionarDespesa(id, requestDTO);

        // 2. Criamos a URI para o cabeçalho Location, apontando para o novo recurso
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath() // Começa da raiz da aplicação
                .path("/cachorros/{cachorroId}/despesas/{despesaId}") // Define o caminho do recurso
                .buildAndExpand(id, despesaCriada.getId())
                .toUri();

        // 3. Retornamos 201 Created, a URI no cabeçalho e o DTO da despesa no corpo
        return ResponseEntity.created(location).body(new DespesaInfoDTO(despesaCriada));
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

    @PatchMapping("/{id}")
    public ResponseEntity<CachorroResponseDTO> atualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos) {
        Cachorro cachorro = cachorroService.atualizarParcial(id, campos);
        return ResponseEntity.ok(new CachorroResponseDTO(cachorro));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CachorroResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid CachorroStatusUpdateDTO dto) {
        Cachorro cachorroAtualizado = cachorroService.atualizarStatus(id, dto.status());
        return ResponseEntity.ok(new CachorroResponseDTO(cachorroAtualizado));
    }

    @PostMapping("/{id}/vender")
    public ResponseEntity<VendaResponseDTO> vender(@PathVariable Long id, @RequestBody @Valid VendaRequestDTO dto) {
        Venda vendaRealizada = vendaService.realizarVenda(id, dto);
        return ResponseEntity.ok(new VendaResponseDTO(vendaRealizada));
    }

    @GetMapping("/{id}/ninhadas")
    public ResponseEntity<List<NinhadaResponseDTO>> listarNinhadasDaMae(@PathVariable Long id) {
        var ninhadas = ninhadaService.listarNinhadasDeUmaMae(id);
        return ResponseEntity.ok(ninhadas);
    }

    @PostMapping("/{id}/vender-flex")
    public ResponseEntity<VendaResponseDTO> venderFlex(@PathVariable Long id, @RequestBody @Valid VendaRequestFlexDTO dto) {
        Venda vendaRealizada = vendaService.realizarVendaFlex(id, dto);
        return ResponseEntity.ok(new VendaResponseDTO(vendaRealizada));
    }
}