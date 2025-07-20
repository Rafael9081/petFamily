// C:/.../frontend/src/service/NinhadaService.ts

import api from './api'; // Your configured Axios instance

// --- Interfaces for API Payloads and Responses ---
// By defining these here, we create a single source of truth for our data structures.

interface Cachorro {
  id: number;
  nome: string;
  raca: string;
  sexo: 'MACHO' | 'FEMEA';
}

interface FilhotePayload {
  nome: string;
  sexo: 'MACHO' | 'FEMEA';
}

interface NinhadaPayload {
  dataNascimento: string;
  maeId: number | null;
  paiId: number | null;
  filhotes: FilhotePayload[];
}

// A generic interface for the paginated data your backend returns
interface PaginatedResponse<T> {
  content: T[];
  // You can add other pagination properties here if needed
  // totalPages: number;
  // totalElements: number;
}

// --- Service Implementation ---

const ninhadaService = {
  /**
   * Fetches a paginated list of dogs, allowing filtering by sex.
   * Ideal for populating 'mother' and 'father' selectors efficiently.
   * @param params - Pagination and filter parameters.
   * @returns A promise with the paginated API response.
   */
  buscarCachorros(params: { sexo: 'MACHO' | 'FEMEA'; page?: number; size?: number }) {
    return api.get<PaginatedResponse<Cachorro>>('/cachorros', { params });
  },

  /**
   * Sends the new litter data to be saved in the backend.
   * @param dadosNinhada - The DTO object (maeId, paiId, dataNascimento, filhotes).
   * @returns A promise with the newly created litter data, including its ID.
   */
  criarNinhada(dadosNinhada: NinhadaPayload) {
    // We expect the backend to return the created object with its new ID
    return api.post<{ id: number }>('/ninhadas', dadosNinhada);
  },

  /**
   * Fetches a paginated list of all registered litters.
   * Perfect for the listing screen (NinhadasView.vue).
   * @param page - The page number (starting from 0).
   * @param size - The number of items per page.
   * @returns A promise with the paginated API response.
   */
  listarNinhadasPaginado(page = 0, size = 10) {
    // This interface matches the data needed for the NinhadasView list
    interface NinhadaListResponse {
      id: number;
      dataNascimento: string;
      mae: { id: number; nome: string };
      pai: { id: number; nome: string };
      totalFilhotes: number;
    }
    return api.get<PaginatedResponse<NinhadaListResponse>>('/ninhadas', { params: { page, size } });
  },

  /**
   * Fetches the complete details of a specific litter by its ID.
   * @param id - The ID of the litter.
   * @returns A promise with the litter's data.
   */
  buscarDetalhesNinhada(id: number) {
    // This interface matches the data needed for the NinhadaDetalhesView
    interface NinhadaDetalhesResponse {
      id: number;
      dataNascimento: string;
      mae: Cachorro;
      pai: Cachorro;
      filhotes: Cachorro[];
    }
    return api.get<NinhadaDetalhesResponse>(`/ninhadas/${id}`);
  }
};

export default ninhadaService;
