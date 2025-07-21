import api from './api';
import type { AxiosResponse } from 'axios';
import type { Tutor } from './CachorroService';

const tutorService = {
  /**
   * Busca uma lista de todos os tutores cadastrados.
   */
  listarTodos(): Promise<AxiosResponse<Tutor[]>> {
    // CORREÇÃO: Remova o prefixo /api para corresponder ao @RequestMapping do backend.
    return api.get('/tutores/todos'); // Use o endpoint específico para listar todos.
  }
};

export default tutorService;
