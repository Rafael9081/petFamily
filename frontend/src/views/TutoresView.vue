<template>
  <div class="view-container">
    <header class="view-header">
      <h1>Gerenciamento de Tutores</h1>
      <router-link to="/tutores/novo" class="btn btn-primary">
        Adicionar Novo Tutor
      </router-link>
    </header>

    <div v-if="loading" class="loading-state">
      <p>Carregando tutores...</p>
    </div>

    <div v-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>

    <div v-if="page && page.content.length > 0" class="table-container">
      <table>
        <thead>
        <tr>
          <th>Nome</th>
          <th>Email</th>
          <th>Telefone</th>
          <th>Cães</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="tutor in page.content" :key="tutor.id" @click="goToDetails(tutor.id)" class="clickable-row">
          <td>{{ tutor.nome }}</td>
          <td>{{ tutor.email }}</td>
          <td>{{ formatarTelefone(tutor.telefone) }}</td>
          <td>{{ tutor.cachorros.length }}</td>
        </tr>
        </tbody>
      </table>

      <!-- Controles de Paginação -->
      <div class="pagination-controls">
        <button @click="changePage(page.number - 1)" :disabled="page.first">
          Anterior
        </button>
        <span>Página {{ page.number + 1 }} de {{ page.totalPages }}</span>
        <!-- CORREÇÃO: Corrigido o erro de digitação -->
        <button @click="changePage(page.number + 1)" :disabled="page.last">
          Próxima
        </button>
      </div>
    </div>

    <div v-if="!loading && (!page || page.content.length === 0)" class="empty-state">
      <p>Nenhum tutor encontrado. Que tal <router-link to="/tutores/novo">adicionar o primeiro</router-link>?</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/service/api';

// ... (Interfaces e lógica do script permanecem as mesmas) ...
interface CachorroInfo { id: number; nome: string; raca: string; }
interface Tutor { id: number; nome: string; email: string; telefone: string; cachorros: CachorroInfo[]; }
interface Page<T> { content: T[]; number: number; size: number; totalPages: number; first: boolean; last: boolean; }

const router = useRouter();
const page = ref<Page<Tutor> | null>(null);
const loading = ref(true);
const errorMessage = ref<string | null>(null);
const currentPage = ref(0);

const fetchTutores = async (pageNumber: number) => {
  loading.value = true;
  errorMessage.value = null;
  try {
    const response = await api.get<Page<Tutor>>(`/tutores?page=${pageNumber}&size=10`);
    page.value = response.data;
  } catch (error) {
    console.error('Falha ao buscar tutores:', error);
    errorMessage.value = 'Não foi possível carregar a lista de tutores.';
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchTutores(currentPage.value);
});

const changePage = (newPage: number) => {
  currentPage.value = newPage;
  fetchTutores(newPage);
};

const goToDetails = (tutorId: number) => {
  router.push({ name: 'tutor-detalhes', params: { id: tutorId } });
};

const formatarTelefone = (telefone: string) => {
  if (!telefone) return 'N/A';
  const digits = telefone.replace(/\D/g, '');

  if (digits.length === 11) {
    return `(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}`;
  }
  if (digits.length === 10) {
    return `(${digits.substring(0, 2)}) ${digits.substring(2, 6)}-${digits.substring(6)}`;
  }
  return telefone;
};
</script>

<style scoped>
/* Os estilos permanecem os mesmos */
.view-container {
  padding: 2rem;
  background-color: #f8f9fa;
}
.view-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}
.view-header h1 {
  margin: 0;
  color: #2c3e50;
  font-weight: 600;
}
.table-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  overflow: hidden;
}
table {
  width: 100%;
  border-collapse: collapse;
}

/* MELHORIA: Regra geral para células */
th, td {
  padding: 1rem 1.25rem;
  text-align: left;
  border-bottom: 1px solid #e9ecef;
  vertical-align: middle; /* Alinhamento vertical consistente */
}

/* MELHORIA DE CONTRASTE: Regra específica para o corpo da tabela */
td {
  color: #212529; /* Cinza bem escuro, quase preto, para máximo contraste */
  font-size: 0.95rem; /* Leve ajuste no tamanho da fonte para mais conforto */
}

tbody tr:last-child td {
  border-bottom: none;
}
th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #495057;
  text-transform: uppercase;
  font-size: 0.85rem;
  letter-spacing: 0.5px;
}
.clickable-row {
  cursor: pointer;
  transition: background-color 0.2s;
}
.clickable-row:hover {
  background-color: #f1f3f5;
}
.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem 0;
  margin-top: 1rem;
}
.pagination-controls button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.pagination-controls button:hover:not(:disabled) {
  background-color: #0056b3;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}
.pagination-controls button:disabled {
  background-color: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
  box-shadow: none;
}
.pagination-controls span {
  color: #495057;
  font-weight: 500;
}
.empty-state {
  text-align: center;
  padding: 3rem;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
.empty-state a {
  color: #007bff;
  font-weight: 500;
  text-decoration: none;
}
.empty-state a:hover {
  text-decoration: underline;
}
.btn-primary {
  background-color: #007bff;
  color: white;
  padding: 0.7rem 1.5rem;
  border-radius: 5px;
  text-decoration: none;
  border: none;
  font-weight: 500;
  transition: background-color 0.2s;
}
.btn-primary:hover {
  background-color: #0056b3;
}
.alert-danger {
  color: #721c24;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  padding: 1rem;
  border-radius: 5px;
}
.loading-state {
  text-align: center;
  padding: 3rem;
  color: #6c757d;
}
</style>
