package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.NinhadaDetalhesDTO;
import br.com.petfamily.canilapi.controller.dto.NinhadaRequestDTO;
import br.com.petfamily.canilapi.controller.dto.NinhadaResponseDTO;
// Removido o import do PaginatedResponseDTO
import br.com.petfamily.canilapi.service.NinhadaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/ninhadas")
public class NinhadaController {

    private final NinhadaService ninhadaService;

    public NinhadaController(NinhadaService ninhadaService) {
        this.ninhadaService = ninhadaService;
    }

    @PostMapping
    public ResponseEntity<NinhadaResponseDTO> criarNinhada(@RequestBody @Valid NinhadaRequestDTO dto) {
        // Idealmente, o serviço já retorna o DTO de resposta.
        // Isso mantém o controller mais limpo e focado no fluxo HTTP.
        // (Requer uma pequena alteração no método criarNinhada do serviço)
        NinhadaResponseDTO ninhadaSalvaDto = ninhadaService.criarNinhada(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ninhadaSalvaDto.id()) // Pega o ID do DTO de resposta
                .toUri();

        return ResponseEntity.created(uri).body(ninhadaSalvaDto);
    }

    @GetMapping
    public ResponseEntity<Page<NinhadaResponseDTO>> listarNinhadas(Pageable pageable) {
        // O serviço já retorna Page<DTO>, que é um padrão robusto.
        Page<NinhadaResponseDTO> pageDeDtos = ninhadaService.listarTodasPaginado(pageable);
        // Retorna a pgina diretamente. O Spring se encarrega de serializar corretamente.
        return ResponseEntity.ok(pageDeDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NinhadaDetalhesDTO> buscarNinhadaPorId(@PathVariable Long id) {
        NinhadaDetalhesDTO ninhadaDTO = ninhadaService.buscarNinhadaPorId(id);
        return ResponseEntity.ok(ninhadaDTO);
    }
}