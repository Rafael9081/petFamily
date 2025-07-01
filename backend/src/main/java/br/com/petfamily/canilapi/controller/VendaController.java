package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.VendaRequest;
import br.com.petfamily.canilapi.model.Venda;
import br.com.petfamily.canilapi.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendas")
@CrossOrigin(origins = "*")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    // A venda é uma ação em um cachorro, então o endpoint reflete isso.
    @PostMapping("/cachorro/{cachorroId}")
    public ResponseEntity<Venda> registrarVenda(@PathVariable Long cachorroId, @RequestBody VendaRequest request) {
        Venda novaVenda = vendaService.registrarVenda(
                cachorroId,
                request.novoTutorId(),
                request.valor()
        );
        return ResponseEntity.status(201).body(novaVenda);
    }
}