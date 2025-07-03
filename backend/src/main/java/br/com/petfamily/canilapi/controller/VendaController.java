package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.VendaRequestDTO;
import br.com.petfamily.canilapi.controller.dto.VendaResponseDTO;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @PostMapping
    public ResponseEntity<VendaResponseDTO> realizarVenda(@RequestBody @Valid VendaRequestDTO dto, UriComponentsBuilder uriBuilder) {
        Venda novaVenda = vendaService.realizarVenda(dto);
        URI uri = uriBuilder.path("/vendas/{id}").buildAndExpand(novaVenda.getId()).toUri();
        return ResponseEntity.created(uri).body(new VendaResponseDTO(novaVenda));
    }
}