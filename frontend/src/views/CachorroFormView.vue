<template>
  <!-- O container agora é um card padronizado -->
  <div class="card">
    <!-- O título agora é um h3, padrpara títulos de card -->
    <h3>{{ isEditMode ? 'Editar Cachorro' : 'Cadastrar Novo Cachorro' }}</h3>

    <form @submit.prevent="handleSubmit">
      <!-- Os form-groups herdam os estilos globais automaticamente -->
      <div class="form-group">
        <label for="nome">Nome:</label>
        <input type="text" id="nome" v-model="formData.nome" required />
      </div>

      <div class="form-group">
        <label for="raca">Raça:</label>
        <input type="text" id="raca" v-model="formData.raca" required />
      </div>

      <div class="form-group">
        <label for="dataNascimento">Data de Nascimento:</label>
        <input type="date" id="dataNascimento" v-model="formData.dataNascimento" required />
      </div>

      <div class="form-group">
        <label for="sexo">Sexo:</label>
        <select id="sexo" v-model="formData.sexo" required>
          <option value="MACHO">Macho</option>
          <option value="FEMEA">Fêmea</option>
        </select>
      </div>

      <!-- As ações do formulário usam os botões padronizados -->
      <div class="form-actions">
        <router-link to="/dashboard" class="btn btn-secondary">Cancelar</router-link>
        <button type="submit" class="btn btn-primary" :disabled="isSaving">
          {{ isSaving ? 'Salvando...' : 'Salvar' }}
        </button>
      </div>

      <!-- A mensagem de erro também herda o estilo global -->
      <p v-if="error" class="error-message">{{ error }}</p>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import cachorroService, { type CachorroPayload } from '@/service/CachorroService';

// --- Roteamento e Props ---
const route = useRoute();
const router = useRouter();
const cachorroId = computed(() => route.params.id ? Number(route.params.id) : null);
const isEditMode = computed(() => !!cachorroId.value);

// --- Estado do Componente ---
const formData = ref<CachorroPayload>({
  nome: '',
  raca: '',
  dataNascimento: '',
  sexo: 'MACHO',
});
const isSaving = ref(false);
const error = ref<string | null>(null);

// --- Lógica do Ciclo de Vida ---
onMounted(async () => {
  if (isEditMode.value && cachorroId.value) {
    try {
      const response = await cachorroService.buscarDetalhes(cachorroId.value);
      const { nome, raca, dataNascimento, sexo } = response.data;
      formData.value = { nome, raca, dataNascimento, sexo };
    } catch (err) {
      error.value = "Não foi possível carregar os dados para edição.";
      console.error(err);
    }
  }
});

// --- Métodos ---
async function handleSubmit() {
  isSaving.value = true;
  error.value = null;
  try {
    let savedCachorro;
    if (isEditMode.value && cachorroId.value) {
      const response = await cachorroService.atualizar(cachorroId.value, formData.value);
      savedCachorro = response.data;
      alert('Cachorro atualizado com sucesso!');
    } else {
      const response = await cachorroService.criar(formData.value);
      savedCachorro = response.data;
      alert('Cachorro cadastrado com sucesso!');
    }
    router.push(`/cachorros/${savedCachorro.id}`);
  } catch (err: any) {
    if (err.response?.data?.message) {
      error.value = `Erro do servidor: ${err.response.data.message}`;
    } else {
      error.value = "Ocorreu um erro ao salvar. Tente novamente.";
    }
    console.error(err);
  } finally {
    isSaving.value = false;
  }
}
</script>

<style scoped>
/*
  TODA a estilização foi movida para o main.css.
  Este componente agora é 100% focado em estrutura e lógica,
  deixando-o muito mais limpo e fácil de manter.
*/
.card {
  max-width: 700px;
  margin: 2rem auto;
}
</style>
