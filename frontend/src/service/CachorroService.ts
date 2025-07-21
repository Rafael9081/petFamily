import api from './api';
import type { AxiosResponse } from 'axios';

// --- Interfaces ---
// Centralizing interfaces makes the service the "source of truth"
// for the data structures related to Cachorro.

export interface CachorroPayload {
  nome: string;
  raca: string;
  dataNascimento: string;
  sexo: 'MACHO' | 'FEMEA';
}

export interface Tutor {
  id: number;
  nome: string;
}

export interface Cachorro {
  id: number;
  nome: string;
  raca: string;
  dataNascimento: string;
  sexo: 'MACHO' | 'FEMEA';
  status: 'DISPONIVEL' | 'RESERVADO' | 'VENDIDO' | 'MATRIZ_PADREADOR' | 'INDISPONIVEL';
  tutor: Tutor | null;
}

export interface Despesa {
  id: number;
  descricao: string;
  valor: number;
  data: string;
}

export interface Venda {
  id: number;
  valor: number;
  data: string;
}

export interface RelatorioFinanceiro {
  custoTotal: number;
  registroVenda?: Venda;
  lucro: number;
  historicoDespesas: Despesa[];
}

export interface Ninhada {
  id: number;
  dataNascimento: string;
  pai?: { nome: string };
  mae?: { nome: string };
  totalFilhotes: number;
}


// --- Service Implementation ---

const cachorroService = {
  /**
   * Creates a new dog.
   * Corresponds to the POST /cachorros endpoint.
   * @param payload The form data for the new dog.
   */
  criar(payload: CachorroPayload): Promise<AxiosResponse<Cachorro>> {
    return api.post('/cachorros', payload);
  },

  /**
   * Updates an existing dog.
   * Corresponds to the PUT /cachorros/{id} endpoint.
   * @param id The ID of the dog to update.
   * @param payload The updated form data.
   */
  atualizar(id: number, payload: CachorroPayload): Promise<AxiosResponse<Cachorro>> {
    return api.put(`/cachorros/${id}`, payload);
  },

  /**
   * Fetches the full details of a specific dog.
   * Corresponds to the GET /cachorros/{id} endpoint.
   * @param id The ID of the dog.
   */
  buscarDetalhes(id: number): Promise<AxiosResponse<Cachorro>> {
    return api.get<Cachorro>(`/cachorros/${id}`);
  },

  /**
   * Fetches the financial report for a dog.
   * Corresponds to the GET /cachorros/{id}/relatorio-financeiro endpoint.
   * @param id The ID of the dog.
   */
  buscarRelatorioFinanceiro(id: number): Promise<AxiosResponse<RelatorioFinanceiro>> {
    return api.get<RelatorioFinanceiro>(`/cachorros/${id}/relatorio-financeiro`);
  },

  /**
   * Lists all litters a dog has participated in (as mother or father).
   * Corresponds to the GET /cachorros/{id}/ninhadas endpoint.
   * @param id The ID of the dog.
   */
  listarNinhadasDoCachorro(id: number): Promise<AxiosResponse<Ninhada[]>> {
    return api.get<Ninhada[]>(`/cachorros/${id}/ninhadas`);
  }
};

export default cachorroService;
