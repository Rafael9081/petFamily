<template>
  <form @submit.prevent="handleVenda" class="venda-form">
    <h4>Registrar Venda</h4>
    <p class="form-description">Selecione o novo tutor e preencha os dados da venda.</p>

    <!-- ATUALIZADO: Campo para selecionar um tutor existente -->
    <div class="form-group">
      <label for="tutor">Novo Tutor</label>
      <select id="tutor" v-model="vendaData.novoTutorId" required>
        <option :value="null" disabled>-- Selecione um tutor --</option>
        <option v-for="tutor in tutorsList" :key="tutor.id" :value="tutor.id">
          {{ tutor.nome }}
        </option>
      </select>
    </div>

    <div class="form-grid">
      <div class="form-group">
        <label for="valorVenda">Valor da Venda (R$)</label>
        <input type="number" id="valorVenda" v-model.number="vendaData.valor" step="0.01" placeholder="Ex: 2500.00" required />
      </div>
      <div class="form-group">
        <label for="dataVenda">Data da Venda</label>
        <input type="date" id="dataVenda" v-model="vendaData.data" required />
      </div>
    </div>

    <div class="form-actions">
      <button type="submit" class="btn btn-primary" :disabled="isSaving">
        {{ isSaving ? 'Registrando...' : 'Confirmar Venda' }}
      </button>
    </div>
    <p v-if="error" class="error-message">{{ error }}</p>
  </form>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import vendaService, { type VendaPayload, type VendaResponse } from '@/service/VendaService';
import tutorService from '@/service/TutorService'; // Precisaremos de um serviço para buscar tutores
import type { Tutor } from '@/service/CachorroService';

const props = defineProps<{
  cachorroId: number;
}>();

const emit = defineEmits<{
  (e: 'venda-concluida', venda: VendaResponse): void;
}>();

// --- Estado do Componente ---
const tutorsList = ref<Tutor[]>([]);
const vendaData = ref<VendaPayload>({
  novoTutorId: null, // ATUALIZADO: Começa como nulo
  valor: 0,
  data: new Date().toISOString().split('T')[0],
});
const isSaving = ref(false);
const error = ref<string | null>(null);

// --- Métodos ---

// Busca a lista de tutores quando o componente é montado
onMounted(async () => {
  try {
    const response = await tutorService.listarTodos();
    tutorsList.value = response.data;
  } catch (err) {
    error.value = "Não foi possível carregar a lista de tutores.";
    console.error(err);
  }
});

async function handleVenda() {
  if (!props.cachorroId || !vendaData.value.novoTutorId) {
    error.value = "Todos os campos são obrigatórios.";
    return;
  }
  isSaving.value = true;
  error.value = null;
  try {
    // ATUALIZADO: A chamada agora passa o ID do cachorro e o payload separadamente
    const response = await vendaService.vender(props.cachorroId, vendaData.value);
    alert('Venda registrada com sucesso!');
    emit('venda-concluida', response.data);
  } catch (err: any) {
    error.value = err.response?.data?.message || "Ocorreu um erro ao registrar a venda.";
    console.error(err);
  } finally {
    isSaving.value = false;
  }
}
</script>

<style scoped>
/* Seu CSS pode permanecer o mesmo */
.venda-form h4 {
  margin-top: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--color-primary-dark);
}
.form-description {
  font-size: 0.9rem;
  color: var(--color-text-secondary);
  margin-top: -0.5rem;
  margin-bottom: 1.5rem;
}
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}
.form-actions {
  justify-content: flex-start;
  border-top: none;
  padding-top: 0.5rem;
  margin-top: 1rem;
}
</style>
