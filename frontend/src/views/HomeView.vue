<script setup lang="ts">
import { ref, onMounted } from 'vue';
import api from '@/services/api';

// --- INTERFACES ---
interface Tutor {
  id: number;
  nome: string;
  email: string;
  telefone: string;
}

// --- ESTADO DA APLICAÇÃO ---
const tutores = ref<Tutor[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

// --- ESTADO DO FORMULÁRIO ---
const novoTutorNome = ref('');
const novoTutorEmail = ref('');
const novoTutorTelefone = ref('');
const formFeedback = ref<{ type: 'success' | 'error'; message: string } | null>(null);

// --- LÓGICA (MÉTODOS) ---

const fetchTutores = async () => {
  try {
    loading.value = true;
    const response = await api.get<Tutor[]>('/tutores');
    tutores.value = response.data;
    error.value = null;
  } catch (err) {
    console.error("Erro ao buscar tutores:", err);
    error.value = "Falha ao carregar os tutores. Verifique se o backend está rodando.";
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async () => {
  if (!novoTutorNome.value || !novoTutorEmail.value) {
    formFeedback.value = { type: 'error', message: "Nome e E-mail são obrigatios." };
    return;
  }

  try {
    formFeedback.value = null;
    const payload = {
      nome: novoTutorNome.value,
      email: novoTutorEmail.value,
      telefone: novoTutorTelefone.value,
    };

    await api.post('/tutores', payload);

    formFeedback.value = { type: 'success', message: "Tutor cadastrado com sucesso!" };
    novoTutorNome.value = '';
    novoTutorEmail.value = '';
    novoTutorTelefone.value = '';
    await fetchTutores();

    setTimeout(() => { formFeedback.value = null; }, 3000);

  } catch (err: any) {
    console.error("Erro ao criar tutor:", err);
    const message = err.response?.data?.message || "Não foi possível cadastrar o tutor.";
    formFeedback.value = { type: 'error', message };
  }
};

// NOVO: Função para excluir um tutor
const handleDelete = async (tutorId: number) => {
  // Confirmação do usuário para evitar exclusões acidentais
  if (!confirm("Tem certeza que deseja excluir este tutor? Esta ação não pode ser desfeita.")) {
    return;
  }

  try {
    await api.delete(`/tutores/${tutorId}`);
    // Recarrega a lista para refletir a exclusão
    await fetchTutores();
  } catch (err) {
    console.error(`Erro ao excluir o tutor ${tutorId}:`, err);
    alert("Não foi possível excluir o tutor. Tente novamente.");
  }
};

onMounted(fetchTutores);
</script>

<template>
  <main>
    <h1>Gerenciamento de Tutores</h1>

    <section class="form-section">
      <h2>Cadastrar Novo Tutor</h2>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="nome">Nome:</label>
          <input id="nome" v-model="novoTutorNome" type="text" required />
        </div>
        <div class="form-group">
          <label for="email">E-mail:</label>
          <input id="email" v-model="novoTutorEmail" type="email" required />
        </div>
        <div class="form-group">
          <label for="telefone">Telefone:</label>
          <input id="telefone" v-model="novoTutorTelefone" type="text" />
        </div>

        <div v-if="formFeedback" :class="`message ${formFeedback.type}`">
          {{ formFeedback.message }}
        </div>

        <button type="submit">Salvar Tutor</button>
      </form>
    </section>

    <hr />

    <section class="list-section">
      <h2>Tutores Cadastrados</h2>
      <div v-if="loading">Carregando...</div>
      <div v-if="error" class="message error">{{ error }}</div>

      <ul v-if="!loading && !error && tutores.length > 0">
        <li v-for="tutor in tutores" :key="tutor.id">
          <div class="tutor-info">
            <strong>{{ tutor.nome }}</strong>
            <span>{{ tutor.email }} - {{ tutor.telefone }}</span>
          </div>
          <!-- NOVO: Botão de Excluir -->
          <button @click="handleDelete(tutor.id)" class="delete-btn">Excluir</button>
        </li>
      </ul>

      <p v-if="!loading && tutores.length === 0">Nenhum tutor cadastrado ainda.</p>
    </section>
  </main>
</template>

<style scoped>
main {
  padding: 2rem;
  font-family: sans-serif;
  max-width: 800px;
  margin: 0 auto;
}

hr {
  margin: 2rem 0;
  border: 0;
  border-top: 1px solid #e0e0e0;
}

.message {
  padding: 1rem;
  border-radius: 4px;
  margin-top: 1rem;
  border: 1px solid;
}

.message.error {
  color: #d32f2f;
  background-color: #ffcdd2;
  border-color: #d32f2f;
}

.message.success {
  color: #155724;
  background-color: #d4edda;
  border-color: #c3e6cb;
}

.form-section {
  background-color: #f9f9f9;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #eee;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

button {
  background-color: #28a745; /* Verde mais vibrante */
  color: white;
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s;
}

button:hover {
  background-color: #218838;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  display: flex; /* NOVO: Alinha o conteúdo e o botão */
  justify-content: space-between; /* NOVO: Espaça os itens */
  align-items: center; /* NOVO: Centraliza verticalmente */
  background-color: #fff;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 0.5rem;
  color: #333;
  border: 1px solid #e0e0e0;
}

.tutor-info {
  display: flex;
  flex-direction: column;
}

.delete-btn {
  background-color: #dc3545; /* Vermelho */
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}

.delete-btn:hover {
  background-color: #c82333;
}
</style>
