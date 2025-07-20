<template>
  <div class="view-container">
    <header class="view-header">
      <h1>Cachorros Cadastrados</h1>
      <!-- Este botão ainda não funcionará, mas já o deixamos pronto -->
      <router-link to="/cachorros/novo" class="btn btn-primary">
        Adicionar Cachorro
      </router-link>
    </header>

    <!-- Feedback Visual para o Usuário -->
    <div v-if="loading" class="loading-message">
      Carregando cachorros...
    </div>
    <div v-if="error" class="error-message">
      {{ error }}
    </div>

    <!-- Container da Tabela e Paginação -->
    <div v-if="!loading && !error && page">
      <div v-if="page.content.length === 0" class="no-data">
        Nenhum cachorro cadastrado ainda.
      </div>

      <div v-else class="table-container">
        <table class="data-table">
          <thead>
          <tr>
            <th>Nome</th>
            <th>Raça</th>
            <th>Status</th>
            <th>Tutor Atual</th>
            <th>Ações</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="cachorro in page.content" :key="cachorro.id">
            <td>{{ cachorro.nome }}</td>
            <td>{{ cachorro.raca }}</td>
            <td>
                <span :class="['status-badge', getStatusClass(cachorro.status)]">
                  {{ formatStatus(cachorro.status) }}
                </span>
            </td>
            <!-- Usando optional chaining (?.) para acessar o nome do tutor de forma segura -->
            <td>{{ cachorro.tutor?.nome || 'Sem tutor' }}</td>
            <td>
              <router-link :to="`/cachorros/${cachorro.id}`" class="btn btn-sm btn-secondary">
                Ver Detalhes
              </router-link>
            </td>
          </tr>
          </tbody>
        </table>

        <!-- Controles de Paginação -->
        <div v-if="page.totalPages > 1" class="pagination-controls">
          <button @click="changePage(page.number - 1)" :disabled="page.first">
            Anterior
          </button>
          <span>Página {{ page.number + 1 }} de {{ page.totalPages }}</span>
          <button @click="changePage(page.number + 1)" :disabled="page.last">
            Próxima
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '@/service/api';

// --- Interfaces para Tipagem de Dados ---

// Deve corresponder ao TutorInfoDTO do backend
interface TutorInfo {
  id: number;
  nome: string;
}

// Deve corresponder ao CachorroResponseDTO do backend
interface Cachorro {
  id: number;
  nome: string;
  raca: string;
  status: 'DISPONIVEL' | 'VENDIDO' | 'RESERVADO'; // Adapte se os nomes forem diferentes
  tutor: TutorInfo | null; // O tutor pode ser nulo
}

// Interface para a resposta paginada do Spring (ou do nosso PaginatedResponseDTO)
interface Page<T> {
  content: T[];
  number: number;
  size: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

// --- Estado Reativo do Componente ---
const loading = ref(true);
const error = ref<string | null>(null);
const page = ref<Page<Cachorro> | null>(null); // Inicia como nulo

const route = useRoute();
const router = useRouter();

// --- Funções ---

// Função principal para buscar dados da API
const fetchCachorros = async (pageNumber = 0) => {
  loading.value = true;
  error.value = null;
  try {
    const response = await api.get<Page<Cachorro>>('/cachorros', {
      params: {
        page: pageNumber,
        size: 10,
        sort: 'nome,asc'
      }
    });
    page.value = response.data;
  } catch (err) {
    console.error("Falha ao carregar cachorros:", err);
    error.value = "Não foi possível carregar a lista de cachorros. Ocorreu um erro no servidor.";
  } finally {
    loading.value = false;
  }
};

// Navega para uma nova página e atualiza a URL
const changePage = (newPage: number) => {
  if (page.value && newPage >= 0 && newPage < page.value.totalPages) {
    router.push({ query: { page: newPage } });
  }
};

// Funções de formatação para a UI
const formatStatus = (status: string) => {
  if (!status) return 'Indefinido';
  return status.charAt(0).toUpperCase() + status.slice(1).toLowerCase().replace('_', ' ');
};

const getStatusClass = (status: string) => {
  if (!status) return 'status-indefinido';
  return `status-${status.toLowerCase()}`;
};


// --- Ciclo de Vida e Observadores ---

// Busca os dados quando o componente é montado pela primeira vez
onMounted(() => {
  const initialPage = Number(route.query.page) || 0;
  fetchCachorros(initialPage);
});

// Observa mudanças no parâmetro 'page' da URL e recarrega os dados
watch(
  () => route.query.page,
  (newPageQuery) => {
    // Só recarrega se não for a primeira montagem (evita chamada dupla)
    if (page.value !== null) {
      const newPageNumber = Number(newPageQuery) || 0;
      fetchCachorros(newPageNumber);
    }
  }
);
</script>

<style scoped>
/* Estilos que já definimos antes, garantindo consistência */
.view-container { padding: 1rem 2rem; }
.view-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e0e0e0;
}
.view-header h1 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.8rem;
  font-weight: 600;
}
.table-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  overflow: hidden;
}
.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th, .data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
  color: #2c3e50; /* Cor escura aplicada a AMBOS, cabeçalho e dados */
  vertical-align: middle; /* Alinhamento vertical aplicado a AMBOS */
}
.data-table th {
  background-color: #f8f9fa;
  font-weight: 600;
}
.no-data, .loading-message, .error-message {
  text-align: center;
  padding: 3rem;
  color: #6c757d;
  background-color: #fff;
  border-radius: 8px;
}
.error-message {
  color: #dc3545;
  background-color: #f8d7da;
}
.status-badge {
  padding: 0.25rem 0.6rem;
  border-radius: 12px;
  color: #fff;
  font-size: 0.8rem;
  font-weight: 500;
}
.status-disponivel { background-color: #28a745; }
.status-vendido { background-color: #6c757d; }
.status-reservado { background-color: #ffc107; color: #212529; }

.pagination-controls {
  padding: 1rem;
  display: flex;
  gap: 1rem;
  justify-content: center;
  align-items: center;
  border-top: 1px solid #e0e0e0;
}
.pagination-controls button {
  padding: 0.5rem 1rem;
  border: 1px solid #dee2e6;
  background-color: #fff;
  border-radius: 4px;
  cursor: pointer;
}
.pagination-controls button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.btn-primary { background-color: #007bff; color: white; padding: 0.5rem 1rem; border-radius: 4px; text-decoration: none; }
.btn-secondary { background-color: #6c757d; color: white; }
.btn-sm { padding: 0.25rem 0.5rem; font-size: 0.875rem; border-radius: 4px; text-decoration: none;}

.status-indefinido {
  background-color: #6c757d; /* Um cinza neutro que funciona bem */
  color: white;
}

/* 2. Solução para o botão não quebrar linha */
.btn-sm {
  white-space: nowrap; /* Impede que o texto dentro do elemento seja quebrado */
}


</style>
