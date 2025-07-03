package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.DespesaResponseDTO; // Importe o novo DTO
import br.com.petfamily.canilapi.model.Despesa;
import br.com.petfamily.canilapi.service.DespesaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/despesas")
public class DespesaController {

    private final DespesaService despesaService;

    public DespesaController(DespesaService despesaService) {
        this.despesaService = despesaService;
    }

    @GetMapping
    // 1. O tipo de retorno agora é uma lista do nosso novo DTO.
    public ResponseEntity<List<DespesaResponseDTO>> listarPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {

        // 2. O serviço continua retornando a lista de entidades.
        List<Despesa> despesas = despesaService.listarDespesasPorPeriodo(inicio, fim);

        // 3. O controller assume a responsabilidade de converter a lista de Entidades para DTOs.
        List<DespesaResponseDTO> dtos = despesas.stream()
                .map(DespesaResponseDTO::new) // Usa o construtor que criamos no DTO
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}