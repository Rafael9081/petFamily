
import api from './api';
import type { AxiosResponse } from 'axios';
import type { Tutor } from './CachorroService';

// --- Interfaces para a Venda ---

// ATUALIZADO: O payload agora reflete o que o backend (VendaRequestDTO) espera.
export interface VendaPayload {
  novoTutorId: number | null;
  valor: number;
  data: string;
}
export interface VendaResponse {
  id: number;
  valor: number;
  data: string;
  tutor: Tutor;
}

// --- Implementação do Serviço ---

const vendaService = {
  /**
   * Registra a venda de um cachorro.
   * @param cachorroId O ID do cachorro que está sendo vendido.
   * @param payload Os dados da venda (ID do tutor, valor, data).
   */
  vender(cachorroId: number, payload: VendaPayload): Promise<AxiosResponse<VendaResponse>> {
    // ATUALIZADO: A URL agora corresponde exatamente ao endpoint do CachorroController.
    const url = `/cachorros/${cachorroId}/vender`;
    return api.post(url, payload);
  }
};

export default vendaService;
