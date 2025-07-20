<template>
  <div class="view-container">
    <header class="view-header">
      <h1>Ninhadas Registradas</h1>
      <p>Visualize todas as ninhadas do seu canil.</p>
    </header>

    <!-- Estado de Carregamento -->
    <div v-if="loading" class="loading-state">
      <p>Buscando ninhadas...</p>
    </div>

    <!-- Estado de Erro -->
    <div v-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>

    <!-- Estado Vazio -->
    <div v-if="!loading && ninhadas.length === 0 && !errorMessage" class="empty-state">
      <h2>Nenhuma ninhada encontrada</h2>
      <p>Parece que você ainda negistrou nenhuma ninhada.</p>
      <router-link to="/ninhadas/nova" class="btn-primary">Registrar a Primeira Ninhada</router-link>
    </div>

    <!-- Lista de Ninhadas -->
    <div v-if="ninhadas.length > 0" class="ninhadas-grid">
      <div v-for="ninhada in ninhadas" :key="ninhada.id" class="ninhada-card">
        <div class="card-header">
          <h3>Ninhada #{{ ninhada.id }}</h3>
          <span class="chip">{{ ninhada.totalFilhotes }} filhotes</span>
        </div>
        <div class="card-body">
          <p><strong>Nascimento:</strong> {{ formatarData(ninhada.dataNascimento) }}</p>
          <p><strong>Mãe:</strong> {{ ninhada.mae.nome }}</p>
          <p><strong>Pai:</strong> {{ ninhada.pai.nome }}</p>
        </div>
        <div class="card-footer">
          <!-- O link para detalhes será implementado no futuro -->
          <router-link :to="`/ninhadas/${ninhada.id}`" class="btn-details">
            Ver Detalhes
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import api from '@/service/api';

// --- Interfaces para os dados da API ---
interface CachorroResumo {
  id: number;
  nome: string;
}

interface NinhadaResponse {
  id: number;
  dataNascimento: string;
  mae: CachorroResumo;
  pai: CachorroResumo;
  totalFilhotes: number;
  filhotes: CachorroResumo[];
}

// --- Estado do Componente ---
const ninhadas = ref<NinhadaResponse[]>([]);
const loading = ref(true);
const errorMessage = ref<string | null>(null);

// --- Lógica de Dados ---
onMounted(async () => {
  try {
    const response = await api.get<NinhadaResponse[]>('/ninhadas');
    ninhadas.value = response.data;
  } catch (error) {
    console.error("Falha ao buscar ninhadas:", error);
    errorMessage.value = "Não foi possível carregar a lista de ninhadas. Tente novamente mais tarde.";
  } finally {
    loading.value = false;
  }
});

// --- Funções Auxiliares ---
const formatarData = (data: string) => {
  if (!data) return 'N/A';
  const [ano, mes, dia] = data.split('-');
  return `${dia}/${mes}/${ano}`;
};
</script>

<style scoped>
.view-container {
  padding: 2rem;
}
.view-header {
  text-align: center;
  margin-bottom: 2.5rem;
}
.view-header h1 {
  margin-bottom: 0.5rem;
}
.ninhadas-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}
.ninhada-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  transition: transform 0.2s, box-shadow 0.2s;
}
.ninhada-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.12);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e9ecef;
}
.card-header h3 {
  margin: 0;
  font-size: 1.2rem;
}
.chip {
  background-color: #e0e7ff;
  color: #4338ca;
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 600;
}
.card-body {
  padding: 1.5rem;
  flex-grow: 1;
}
.card-body p {
  margin: 0 0 0.75rem 0;
}
.card-footer {
  padding: 1rem 1.5rem;
  background-color: #f8f9fa;
  text-align: right;
}
.btn-details {
  background-color: #007bff;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 5px;
  text-decoration: none;
  font-weight: 500;
}
.loading-state, .empty-state {
  text-align: center;
  padding: 3rem;
  color: #6c757d;
}
.empty-state h2 {
  margin-bottom: 1rem;
}
.btn-primary {
  background-color: #007bff;
  color: white;
  padding: 0.7rem 1.5rem;
  border-radius: 5px;
  text-decoration: none;
  font-weight: 500;
  display: inline-block;
  margin-top: 1rem;
}
.alert-danger {
  color: #721c24;
  background-color: #f8d7da;
  border-color: #f5c6cb;
  padding: 1rem;
  margin-bottom: 1.5rem;
  border-radius: 5px;
}
</style>
