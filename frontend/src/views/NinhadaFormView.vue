<template>
  <!-- O template HTML não muda -->
  <div class="form-view">
    <header class="view-header">
      <h1>Registrar Nova Ninhada</h1>
      <p>Selecione os pais, a data de nascimento e adicione os filhotes.</p>
    </header>
    <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    <form @submit.prevent="handleSubmit" class="form-container" v-if="!loadingInitialData">
      <div class="ninhada-details-grid">
        <div class="form-group">
          <label for="mae">Mãe (Fêmea)</label>
          <select id="mae" v-model="ninhadaData.maeId" required>
            <option disabled :value="null">Selecione uma mãe</option>
            <option v-for="mae in maesDisponiveis" :key="mae.id" :value="mae.id">
              {{ mae.nome }} ({{ mae.raca }})
            </option>
          </select>
        </div>
        <div class="form-group">
          <label for="pai">Pai (Macho)</label>
          <select id="pai" v-model="ninhadaData.paiId" required>
            <option disabled :value="null">Selecione um pai</option>
            <option v-for="pai in paisDisponiveis" :key="pai.id" :value="pai.id">
              {{ pai.nome }} ({{ pai.raca }})
            </option>
          </select>
        </div>
        <div class="form-group">
          <label for="dataNascimento">Data de Nascimento</label>
          <input id="dataNascimento" v-model="ninhadaData.dataNascimento" type="date" required />
        </div>
      </div>
      <div class="filhotes-section">
        <h3>Filhotes</h3>
        <div v-if="ninhadaData.filhotes.length === 0" class="empty-state">
          Nenhum filhote adicionado. Clique no botão abaixo para começar.
        </div>
        <div v-for="(filhote, index) in ninhadaData.filhotes" :key="index" class="filhote-row">
          <input type="text" v-model="filhote.nome" placeholder="Nome do Filhote" required />
          <select v-model="filhote.sexo" required>
            <option value="MACHO">Macho</option>
            <option value="FEMEA">Fêmea</option>
          </select>
          <button type="button" @click="removerFilhote(index)" class="btn-remove">Remover</button>
        </div>
        <button type="button" @click="adicionarFilhote" class="btn-add-filhote">
          + Adicionar Filhote
        </button>
      </div>
      <div class="form-actions">
        <button type="submit" class="btn-primary" :disabled="isSubmitting || ninhadaData.filhotes.length === 0">
          {{ isSubmitting ? 'Salvando...' : 'Salvar Ninhada' }}
        </button>
        <router-link to="/ninhadas" class="btn-secondary">Cancelar</router-link>
      </div>
    </form>
    <div v-if="loadingInitialData" class="loading-state">
      Carregando dados...
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import ninhadaService from '@/service/NinhadaService';

// Interfaces e refs permanecem os mesmos...
interface Cachorro { id: number; nome: string; raca: string; sexo: 'MACHO' | 'FEMEA'; }
interface FilhotePayload { nome: string; sexo: 'MACHO' | 'FEMEA'; }
interface NinhadaPayload { dataNascimento: string; maeId: number | null; paiId: number | null; filhotes: FilhotePayload[]; }

const router = useRouter();
const maesDisponiveis = ref<Cachorro[]>([]);
const paisDisponiveis = ref<Cachorro[]>([]);
const loadingInitialData = ref(true);
const isSubmitting = ref(false);
const errorMessage = ref<string | null>(null);
const successMessage = ref<string | null>(null);

const ninhadaData = ref<NinhadaPayload>({ dataNascimento: '', maeId: null, paiId: null, filhotes: [] });

onMounted(async () => {
  try {
    const [maesResponse, paisResponse] = await Promise.all([
      ninhadaService.buscarCachorros({ sexo: 'FEMEA' }),
      ninhadaService.buscarCachorros({ sexo: 'MACHO' })
    ]);

    // --- CORREÇÃO APLICADA AQUI ---
    maesDisponiveis.value = maesResponse.data?.content || [];
    paisDisponiveis.value = paisResponse.data?.content || []; // Corrigido para usar a resposta correta

  } catch (error) {
    errorMessage.value = 'Falha ao carregar a lista de cães para seleção.';
    console.error(error);
  } finally {
    loadingInitialData.value = false;
  }
});

// O resto do seu script (handleSubmit, etc.) já está correto e não precisa de alterações.
const adicionarFilhote = () => { ninhadaData.value.filhotes.push({ nome: '', sexo: 'MACHO' }); };
const removerFilhote = (index: number) => { ninhadaData.value.filhotes.splice(index, 1); };
const resetForm = () => { ninhadaData.value = { dataNascimento: '', maeId: null, paiId: null, filhotes: [] }; };

const handleSubmit = async () => {
  if (ninhadaData.value.maeId === ninhadaData.value.paiId && ninhadaData.value.maeId !== null) {
    errorMessage.value = 'A mãe e o pai não podem ser o mesmo cachorro. Por favor, selecione pais diferentes.';
    return;
  }

  isSubmitting.value = true;
  errorMessage.value = null;
  successMessage.value = null;

  try {
    const response = await ninhadaService.criarNinhada(ninhadaData.value);
    successMessage.value = `Ninhada registrada com sucesso! ID: ${response.data.id}. Redirecionando...`;
    resetForm();
    setTimeout(() => {
      router.push(`/ninhadas/${response.data.id}`);
    }, 2000);
  } catch (err: any) {
    const validationErrors = err.response?.data?.errors;
    if (validationErrors) {
      errorMessage.value = validationErrors.map((e: any) => e.defaultMessage).join(' ');
    } else {
      errorMessage.value = err.response?.data?.message || 'Ocorreu um erro ao salvar a ninhada.';
    }
    console.error(err);
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
/* Estilos específicos para o layout do formulário */
.form-view {
  background-color: var(--card-background);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow);
  max-width: 800px;
}

.view-header {
  text-align: center;
}

.ninhada-details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.form-group input, .form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: 5px;
  font-size: 1rem;
}

.filhotes-section {
  margin-top: 2.5rem;
  border-top: 1px solid var(--border-color);
  padding-top: 1.5rem;
}

.filhotes-section h3 {
  margin-bottom: 1rem;
}

.filhote-row {
  display: flex;
  gap: 1rem;
  align-items: center;
  margin-bottom: 1rem;
}

.filhote-row input, .filhote-row select {
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: 5px;
}

.filhote-row input { flex-grow: 1; }

.btn-add-filhote {
  display: block;
  width: 100%;
  padding: 0.8rem;
  margin-top: 1rem;
  background-color: #e9ecef;
  border: 1px dashed var(--text-color-light);
  color: var(--text-color-dark);
  cursor: pointer;
  border-radius: 5px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}
</style>
