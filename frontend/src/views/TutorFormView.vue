<template>
  <div class="form-view">
    <header class="view-header">
      <h1>{{ isEditMode ? 'Editar Tutor' : 'Cadastrar Novo Tutor' }}</h1>
      <p>
        {{ isEditMode ? 'Altere os dados abaixo para atualizar o registro.' : 'Preencha os dados para adicionar um novo tutor.' }}
      </p>
    </header>

    <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

    <form @submit.prevent="handleSubmit" class="form-container">
      <div class="form-group">
        <label for="nome">Nome Completo</label>
        <input id="nome" v-model="tutorData.nome" type="text" required />
      </div>

      <div class="form-group">
        <label for="email">Email</label>
        <input id="email" v-model="tutorData.email" type="email" required />
      </div>

      <div class="form-group">
        <label for="telefone">Telefone</label>
        <!-- MUDANÇA: Removemos v-maska e data-maska. O placeholder agora é a única guia. -->
        <input id="telefone" v-model="tutorData.telefone" type="tel" placeholder="Apenas números (10 ou 11 dígitos)" required />
      </div>

      <div class="form-actions">
        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? 'Salvando...' : (isEditMode ? 'Salvar Alterações' : 'Salvar Tutor') }}
        </button>
        <router-link to="/tutores" class="btn-secondary">Cancelar</router-link>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import api from '@/service/api';

// MUDANÇA: A importação do vMaska foi removida.
// import { vMaska } from "maska";

// --- Interfaces para os dados do Tutor ---
interface TutorPayload {
  nome: string;
  email: string;
  telefone: string;
}
interface TutorResponse extends TutorPayload {
  id: number;
}

// --- Lógica de Rota e Modo de Edição ---
const router = useRouter();
const route = useRoute();
const tutorId = computed(() => route.params.id as string | undefined);
const isEditMode = computed(() => !!tutorId.value);

// --- Estado do Componente ---
const tutorData = ref<TutorPayload>({
  nome: '',
  email: '',
  telefone: ''
});
const loading = ref(false);
const errorMessage = ref<string | null>(null);
const successMessage = ref<string | null>(null);

// --- Lógica para buscar os dados ao carregar a página em modo de edição ---
onMounted(async () => {
  if (isEditMode.value) {
    loading.value = true;
    try {
      const response = await api.get<TutorResponse>(`/tutores/${tutorId.value}`);
      // MUDANÇA: Limpamos o telefone para edição manual, caso ele venha formatado do backend
      tutorData.value = {
        nome: response.data.nome,
        email: response.data.email,
        telefone: response.data.telefone.replace(/\D/g, '') // Mostra apenas os dígitos no campo
      };
    } catch (err) {
      errorMessage.value = 'Falha ao carregar os dados do tutor para edição.';
      console.error(err);
    } finally {
      loading.value = false;
    }
  }
});

// --- Função de envio para lidar com POST (criação) e PUT (atualização) ---
const handleSubmit = async () => {
  loading.value = true;
  errorMessage.value = null;
  successMessage.value = null;

  try {
    let response;
    if (isEditMode.value) {
      response = await api.put(`/tutores/${tutorId.value}`, tutorData.value);
      successMessage.value = `Tutor "${response.data.nome}" atualizado com sucesso!`;
    } else {
      response = await api.post('/tutores', tutorData.value);
      successMessage.value = `Tutor "${response.data.nome}" cadastrado com sucesso!`;
    }

    setTimeout(() => {
      router.push({ name: 'tutor-detalhes', params: { id: response.data.id } });
    }, 1500);

  } catch (err: any) {
    const validationErrors = err.response?.data?.errors;
    if (validationErrors && Array.isArray(validationErrors)) {
      errorMessage.value = validationErrors.map((e: any) => e.defaultMessage).join(' ');
    } else {
      errorMessage.value = `Falha ao ${isEditMode.value ? 'atualizar' : 'cadastrar'} o tutor.`;
    }
    console.error(err);
  } finally {
    if (!successMessage.value) {
      loading.value = false;
    }
  }
};
</script>

<style scoped>
/* Estilos permanecem os mesmos */
.form-view {
  max-width: 700px;
  margin: 2rem auto;
  padding: 2rem;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.view-header {
  text-align: center;
  margin-bottom: 2rem;
}

.view-header h1 {
  margin-bottom: 0.5rem;
  color: #212529;
}

.view-header p {
  color: #495057;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 1rem;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-primary, .btn-secondary {
  padding: 0.7rem 1.5rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 500;
  text-decoration: none;
  display: inline-block;
  font-size: 1rem;
  text-align: center;
}

.btn-primary {
  background-color: #007bff;
  color: white;
  transition: background-color 0.2s;
}
.btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
}
.btn-primary:disabled {
  background-color: #a0cffc;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
  transition: background-color 0.2s;
}
.btn-secondary:hover {
  background-color: #5a6268;
}

.alert {
  padding: 1rem;
  margin-bottom: 1.5rem;
  border-radius: 5px;
  border: 1px solid transparent;
}
.alert-success {
  color: #155724;
  background-color: #d4edda;
  border-color: #c3e6cb;
}
.alert-danger {
  color: #721c24;
  background-color: #f8d7da;
  border-color: #f5c6cb;
}
</style>
