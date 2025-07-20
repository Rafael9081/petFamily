<template>
  <!-- O template HTML não muda -->
  <div class="view-container">
    <div v-if="loading" class="loading-state">Carregando detalhes da ninhada...</div>
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    <div v-if="ninhada && !loading">
      <header class="view-header">
        <h1>Detalhes da Ninhada #{{ ninhada.id }}</h1>
        <router-link to="/ninhadas" class="btn btn-secondary">Voltar para a Lista</router-link>
      </header>
      <div class="details-grid">
        <div class="detail-item">
          <span>Mãe</span>
          <strong>{{ ninhada.mae.nome }}</strong>
        </div>
        <div class="detail-item">
          <span>Pai</span>
          <strong>{{ ninhada.pai.nome }}</strong>
        </div>
        <div class="detail-item">
          <span>Data de Nascimento</span>
          <strong>{{ formatarData(ninhada.dataNascimento) }}</strong>
        </div>
      </div>
      <div class="filhotes-list">
        <h2>Filhotes ({{ ninhada.filhotes.length }})</h2>
        <table class="table">
          <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Sexo</th>
            <th>Ações</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="filhote in ninhada.filhotes" :key="filhote.id">
            <td>{{ filhote.id }}</td>
            <td>{{ filhote.nome }}</td>
            <td>{{ filhote.sexo }}</td>
            <td>
              <router-link :to="`/cachorros/${filhote.id}`" class="btn-details">Ver Detalhes</router-link>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
// CORRECTED IMPORT: No file extension needed.
import ninhadaService from '@/service/NinhadaService';

// ... rest of the script is correct
interface CachorroResumo { id: number; nome: string; sexo: 'MACHO' | 'FEMEA'; }
interface NinhadaDetalhesResponse {
  id: number;
  dataNascimento: string;
  mae: CachorroResumo;
  pai: CachorroResumo;
  filhotes: CachorroResumo[];
}

const route = useRoute();
const ninhada = ref<NinhadaDetalhesResponse | null>(null);
const loading = ref(true);
const errorMessage = ref<string | null>(null);

onMounted(async () => {
  const ninhadaId = Number(route.params.id);
  if (!ninhadaId) {
    errorMessage.value = "ID da ninhada invlido.";
    loading.value = false;
    return;
  }
  try {
    const response = await ninhadaService.buscarDetalhesNinhada(ninhadaId);
    ninhada.value = response.data;
  } catch (error) {
    console.error("Falha ao buscar detalhes da ninhada:", error);
    errorMessage.value = "Não foi possível encontrar a ninhada solicitada.";
  } finally {
    loading.value = false;
  }
});

const formatarData = (data: string) => {
  if (!data) return 'N/A';
  return new Date(data).toLocaleDateString('pt-BR', { timeZone: 'UTC' });
};
</script>

<style scoped>
/* Apenas os estilos específicos para o layout desta página */
.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  background-color: var(--card-background);
  padding: 2rem;
  border-radius: var(--border-radius);
  box-shadow: var(--shadow);
  margin-bottom: 2.5rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.detail-item span {
  color: var(--text-color-light);
  margin-bottom: 0.25rem;
}

.detail-item strong {
  font-size: 1.1rem;
}

.filhotes-list {
  margin-top: 2rem;
}
</style>
