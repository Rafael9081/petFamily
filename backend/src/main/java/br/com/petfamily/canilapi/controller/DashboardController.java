package br.com.petfamily.canilapi.controller;

import br.com.petfamily.canilapi.controller.dto.AtividadeRecenteDTO;
import br.com.petfamily.canilapi.controller.dto.DashboardStatsDTO;
import br.com.petfamily.canilapi.controller.dto.FinanceiroDashboardDTO;
import br.com.petfamily.canilapi.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Endpoint para obter as estatísticas principais para os cards.
     * Rota: GET /dashboard/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        DashboardStatsDTO stats = dashboardService.getStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Endpoint para obter o resumo financeiro dos últimos 30 dias.
     * Rota: GET /dashboard/financeiro-30-dias
     */
    @GetMapping("/financeiro-30-dias")
    public ResponseEntity<FinanceiroDashboardDTO> getFinanceiroResumo() {
        FinanceiroDashboardDTO dto = dashboardService.getFinanceiroUltimos30Dias();
        return ResponseEntity.ok(dto);
    }

    /**
     * Endpoint para obter as 5 atividades mais recentes do sistema.
     * Rota: GET /dashboard/atividades-recentes
     */
    @GetMapping("/atividades-recentes")
    public ResponseEntity<List<AtividadeRecenteDTO>> getAtividadesRecentes() {
        List<AtividadeRecenteDTO> atividades = dashboardService.getAtividadesRecentes();
        return ResponseEntity.ok(atividades);
    }
}