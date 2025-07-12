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

// --- ESTADO DO FORMULÁRIO DE CRIAÇÃO ---
const novoTutorNome = ref('');
const novoTutorEmail = ref('');
const novoTutorTelefone = ref('');
const formFeedback = ref<{ type: 'success' | 'error'; message: string } | null>(null);

// --- NOVO: ESTADO DO MODO DE EDIÇÃO ---
const editingTutorId = ref<number | null>(null); // Guarda o ID do tutor sendo editado
const editingTutorData = ref<Partial<Tutor>>({}); // Guarda os dados do formulário de edição

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
    const payload = { nome: novoTutorNome.value, email: novoTutorEmail.value, telefone: novoTutorTelefone.value };
    await api.post('/tutores', payload);
    formFeedback.value = { type: 'success', message: "Tutor cadastrado com sucesso!" };
    novoTutorNome.value = '';
    novoTutorEmail.value = '';
    novoTutorTelefone.value = '';
    await fetchTutores();
    setTimeout(() => { formFeedback.value = null; }, 3000);
  } catch (err: any) {
    const message = err.response?.data?.message || "Não foi possível cadastrar o tutor.";
    formFeedback.value = { type: 'error', message };
  }
};

const handleDelete = async (tutorId: number) => {
  if (!confirm("Tem certeza que deseja excluir este tutor?")) return;
  try {
    await api.delete(`/tutores/${tutorId}`);
    await fetchTutores();
  } catch (err) {
    console.error(`Erro ao excluir o tutor ${tutorId}:`, err);
    alert("Não foi possível excluir o tutor.");
  }
};

// --- NOVAS FUNÇÕES PARA UPDATE ---

// Inicia o modo de edição para um tutor
const startEditing = (tutor: Tutor) => {
  editingTutorId.value = tutor.id;
  // Cria uma cópia dos dados para edição, para não alterar a lista original diretamente
  editingTutorData.value = { ...tutor };
};

// Cancela o modo de edição
const cancelEditing = () => {
  editingTutorId.value = null;
  editingTutorData.value = {};
};

// Salva as alterações do tutor
const handleUpdate = async () => {
  if (!editingTutorId.value || !editingTutorData.value.nome || !editingTutorData.value.email) {
    alert("Nome e E-mail são obrigatórios para atualizar.");
    return;
  }
  try {
    const id = editingTutorId.value;
    const payload = {
      nome: editingTutorData.value.nome,
      email: editingTutorData.value.email,
      telefone: editingTutorData.value.telefone,
    };
    await api.put(`/tutores/${id}`, payload);

    // Sai do modo de edição e recarrega a lista
    cancelEditing();
    await fetchTutores();

  } catch (err) {
    console.error(`Erro ao atualizar o tutor ${editingTutorId.value}:`, err);
    alert("Não foi possível atualizar os dados do tutor.");
  }
};

onMounted(fetchTutores);
</script>

<template>
  <main>
    <h1>Gerenciamento de Tutores</h1>

    <!-- Formulário de Criação (sem alterações) -->
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

    <!-- Lista de Tutores (com lógica de edição) -->
    <section class="list-section">
      <h2>Tutores Cadastrados</h2>
      <div v-if="loading">Carregando...</div>
      <div v-if="error" class="message error">{{ error }}</div>

      <ul v-if="!loading && !error && tutores.length > 0">
        <li v-for="tutor in tutores" :key="tutor.id" class="tutor-item">

          <!-- MODO DE VISUALIZAÇÃO -->
          <div v-if="editingTutorId !== tutor.id" class="tutor-display">
            <div class="tutor-info">
              <strong>{{ tutor.nome }}</strong>
              <span>{{ tutor.email }} - {{ tutor.telefone }}</span>
            </div>
            <div class="tutor-actions">
              <button @click="startEditing(tutor)" class="edit-btn">Editar</button>
              <button @click="handleDelete(tutor.id)" class="delete-btn">Excluir</button>
            </div>
          </div>

          <!-- MODO DE EDIÇÃO -->
          <div v-else class="tutor-edit-form">
            <input v-model="editingTutorData.nome" type="text" placeholder="Nome" class="edit-input"/>
            <input v-model="editingTutorData.email" type="email" placeholder="E-mail" class="edit-input"/>
            <input v-model="editingTutorData.telefone" type="text" placeholder="Telefone" class="edit-input"/>
            <div class="tutor-actions">
              <button @click="handleUpdate" class="save-btn">Salvar</button>
              <button @click="cancelEditing" class="cancel-btn">Cancelar</button>
            </div>
          </div>
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
  color: black; /* <-- ADICIONADO AQUI */
}

button {
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
  margin-left: 0.5rem;
}

.form-section button {
  background-color: #28a745;
  font-size: 1rem;
  padding: 0.8rem 1.5rem;
  margin-left: 0;
}
.form-section button:hover {
  background-color: #218838;
}

ul {
  list-style: none;
  padding: 0;
}

.tutor-item {
  background-color: #fff;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 0.5rem;
  border: 1px solid #e0e0e0;
}

.tutor-display, .tutor-edit-form {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.tutor-info {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  gap: 0.25rem;
  color: #222;
}

.tutor-actions {
  display: flex;
  flex-shrink: 0;
}

.edit-btn { background-color: #007bff; }
.edit-btn:hover { background-color: #0069d9; }

.delete-btn { background-color: #dc3545; }
.delete-btn:hover { background-color: #c82333; }

.save-btn { background-color: #28a745; }
.save-btn:hover { background-color: #218838; }

.cancel-btn { background-color: #6c757d; }
.cancel-btn:hover { background-color: #5a6268; }

.edit-input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  flex-grow: 1;
  color: black; /* <-- E ADICIONADO AQUI */
}

.form-section h2 {
  color: #222;
}
</style>
