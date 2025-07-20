<template>
  <div class="details-view">
    <div v-if="loading" class="loading-state">
      <p>Carregando dados do tutor...</p>
    </div>

    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

    <div v-if="tutor" class="container">
      <header class="details-header">
        <h1>{{ tutor.nome }}</h1>
        <div class="header-actions">
          <router-link :to="`/tutores/${tutor.id}/editar`" class="btn btn-secondary">
            Editar
          </router-link>
          <button @click="handleDelete" class="btn btn-danger" :disabled="isDeleting">
            {{ isDeleting ? 'Excluindo...' : 'Excluir Tutor' }}
          </button>
        </div>
      </header>

      <div class="details-grid">
        <!-- Card de Informações do Tutor -->
        <div class="card">
          <div class="card-header">
            <h3>Informações de Contato</h3>
          </div>
          <div class="card-body">
            <p><strong>Email:</strong> {{ tutor.email }}</p>
            <!-- MUDANÇA: Usando a função para formatar o telefone -->
            <p><strong>Telefone:</strong> {{ formatarTelefone(tutor.telefone) }}</p>
          </div>
        </div>

        <!-- Card de Cães Associados -->
        <div class="card">
          <div class="card-header">
            <h3>Cães Associados ({{ tutor.cachorros.length }})</h3>
          </div>
          <div class="card-body">
            <ul v-if="tutor.cachorros.length > 0" class="dog-list">
              <li v-for="cachorro in tutor.cachorros" :key="cachorro.id">
                <router-link :to="`/cachorros/${cachorro.id}`">
                  {{ cachorro.nome }} ({{ cachorro.raca }})
                </router-link>
              </li>
            </ul>
            <p v-else>Este tutor ainda não possui cães cadastrados.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '@/service/api';

interface CachorroInfo {
  id: number;
  nome: string;
  raca: string;
}

interface TutorResponse {
  id: number;
  nome: string;
  email: string;
  telefone: string;
  cachorros: CachorroInfo[];
}

const route = useRoute();
const router = useRouter();
const tutorId = route.params.id;

const tutor = ref<TutorResponse | null>(null);
const loading = ref(true);
const isDeleting = ref(false);
const errorMessage = ref<string | null>(null);

onMounted(async () => {
  try {
    const response = await api.get<TutorResponse>(`/tutores/${tutorId}`);
    tutor.value = response.data;
  } catch (error) {
    console.error('Falha ao buscar dados do tutor:', error);
    errorMessage.value = 'Noi possível carregar os dados do tutor. Tente novamente mais tarde.';
  } finally {
    loading.value = false;
  }
});

// MUDANÇA: Adicionando a função de formatação para consistência visual
const formatarTelefone = (telefone: string) => {
  if (!telefone) return 'N/A';
  const digits = telefone.replace(/\D/g, '');

  if (digits.length === 11) {
    return `(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}`;
  }
  if (digits.length === 10) {
    return `(${digits.substring(0, 2)}) ${digits.substring(2, 6)}-${digits.substring(6)}`;
  }
  return telefone; // Retorna o original se não corresponder
};

const handleDelete = async () => {
  if (!tutor.value) return;

  const confirmation = window.confirm(
    `Tem certeza que deseja excluir o tutor "${tutor.value.nome}"? Esta aão pode ser desfeita.`
  );

  if (confirmation) {
    isDeleting.value = true;
    errorMessage.value = null;
    try {
      await api.delete(`/tutores/${tutor.value.id}`);
      alert('Tutor excludo com sucesso!');
      router.push('/tutores');
    } catch (error: any) {
      console.error('Erro ao excluir tutor:', error);
      const backendError = error.response?.data?.message || 'Ocorreu um erro desconhecido.';
      errorMessage.value = `Falha ao excluir: ${backendError}`;
    } finally {
      isDeleting.value = false;
    }
  }
};
</script>

<style scoped>
.details-view {
  padding: 2rem;
  background-color: #f4f7f6;
}
.container {
  max-width: 900px;
  margin: 0 auto;
}
.details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #e0e0e0;
}
.details-header h1 {
  margin: 0;
  color: #2c3e50;
}
.header-actions {
  display: flex;
  gap: 1rem;
}
.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}
.card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  overflow: hidden;
}
.card-header {
  background-color: #f8f9fa;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e9ecef;
}
.card-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #343a40;
}
.card-body {
  padding: 1.5rem;
}
.card-body p {
  margin: 0 0 0.75rem 0;
  color: #495057;
}
.dog-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.dog-list li a {
  display: block;
  padding: 0.75rem 1rem;
  margin-bottom: 0.5rem;
  background-color: #e9ecef;
  border-radius: 5px;
  text-decoration: none;
  color: #007bff;
  transition: background-color 0.2s, color 0.2s;
}
.dog-list li a:hover {
  background-color: #007bff;
  color: white;
}
.btn-secondary { background-color: #6c757d; color: white; padding: 0.6rem 1.2rem; border-radius: 5px; text-decoration: none; border: none; }
.btn-danger { background-color: #dc3545; color: white; padding: 0.6rem 1.2rem; border-radius: 5px; border: none; cursor: pointer; }
.btn-danger:disabled { background-color: #f5c6cb; cursor: not-allowed; }
.alert {
  padding: 1rem;
  margin-bottom: 1.5rem;
  border-radius: 5px;
  border: 1px solid transparent;
}
.alert-danger {
  color: #721c24;
  background-color: #f8d7da;
  border-color: #f5c6cb;
}
.loading-state {
  text-align: center;
  padding: 3rem;
  color: #6c757d;
}
</style>
