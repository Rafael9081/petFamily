<template>
  <!-- O template HTML não muda -->
  <div class="view-container">
    <header class="view-header">
      <h1>Ninhadas Registradas</h1>
      <router-link to="/ninhadas/nova" class="btn btn-primary">
        + Registrar Nova Ninhada
      </router-link>
    </header>

    <div v-if="loading" class="loading-state">
      <p>Buscando ninhadas...</p>
    </div>
    <div v-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>
    <div v-if="!loading && ninhadas.length === 0 && !errorMessage" class="empty-state">
      <h2>Nenhuma ninhada encontrada</h2>
      <p>Parece que você ainda não registrou nenhuma ninhada.</p>
      <router-link to="/ninhadas/nova" class="btn-primary">Registrar a Primeira Ninhada</router-link>
    </div>

    <div v-if="ninhadas.length > 0" class="ninhadas-grid">
      <!-- A classe 'ninhada-card' foi trocada para a genérica 'card' -->
      <div v-for="ninhada in ninhadas" :key="ninhada.id" class="card">
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
// CORRECTED IMPORT: No file extension needed.
import ninhadaService from '@/service/NinhadaService';

// ... rest of the script is correct
interface CachorroResumo { id: number; nome: string; }
interface NinhadaResponse {
  id: number;
  dataNascimento: string;
  mae: CachorroResumo;
  pai: CachorroResumo;
  totalFilhotes: number;
}

const ninhadas = ref<NinhadaResponse[]>([]);
const loading = ref(true);
const errorMessage = ref<string | null>(null);

onMounted(async () => {
  try {
    const response = await ninhadaService.listarNinhadasPaginado(0, 20);
    ninhadas.value = response.data?.content || response.data || [];
  } catch (error) {
    console.error("Falha ao buscar ninhadas:", error);
    errorMessage.value = "Não foi possível carregar a lista de ninhadas. Tente novamente mais tarde.";
  } finally {
    loading.value = false;
  }
});

const formatarData = (data: string) => {
  if (!data) return 'N/A';
  const [ano, mes, dia] = data.split('-');
  return `${dia}/${mes}/${ano}`;
};
</script>

<style scoped>
/* Estilos muito mais limpos! Apenas o que é único para esta tela. */
.ninhadas-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.chip {
  background-color: #e0e7ff; /* Cor específica para este chip */
  color: #4338ca;
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 600;
}
</style>
