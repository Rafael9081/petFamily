<template>
  <div class="form-view">
    <div class="container">
      <header>
        <!-- Título dinâmico para clareza -->
        <h1>{{ isEditMode ? 'Editar Cachorro' : 'Cadastrar Novo Cachorro' }}</h1>
        <p class="subtitle">
          {{ isEditMode ? 'Altere os dados abaixo para atualizar o animal.' : 'Preencha os dados abaixo para adicionar um novo animal.' }}
        </p>
      </header>

      <form @submit.prevent="handleSubmit">
        <div class="form-grid">
          <!-- Campo Nome -->
          <div class="form-group">
            <label for="nome">Nome</label>
            <input id="nome" v-model="cachorroData.nome" type="text" placeholder="Ex: Max, Bella" required />
          </div>

          <!-- Campo Raça -->
          <div class="form-group">
            <label for="raca">Raça</label>
            <input id="raca" v-model="cachorroData.raca" type="text" placeholder="Ex: Golden Retriever" required />
          </div>

          <!-- Campo Data de Nascimento -->
          <div class="form-group">
            <label for="dataNascimento">Data de Nascimento</label>
            <input id="dataNascimento" v-model="cachorroData.dataNascimento" type="date" required />
          </div>

          <!-- Campo Sexo -->
          <div class="form-group">
            <label for="sexo">Sexo</label>
            <select id="sexo" v-model="cachorroData.sexo" required>
              <option disabled value="">Selecione o sexo</option>
              <option value="MACHO">Macho</option>
              <option value="FEMEA">Fêmea</option>
            </select>
          </div>
        </div>

        <!-- Mensagens de feedback -->
        <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>
        <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

        <div class="form-actions">
          <router-link to="/cachorros" class="btn btn-secondary">Cancelar</router-link>
          <button type="submit" class="btn-primary" :disabled="loading">
            <!-- Texto do botão dinâmico -->
            {{ loading ? 'Salvando...' : (isEditMode ? 'Salvar Alterações' : 'Salvar Cachorro') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import api from '@/service/api';

// --- Interfaces ---
// Payload que o formulário envia para o backend (POST ou PATCH)
interface CachorroPayload {
  nome: string;
  raca: string;
  dataNascimento: string;
  sexo: 'MACHO' | 'FEMEA' | '';
}
// Resposta que o backend envia (GET /cachorros/:id)
interface CachorroResponse {
  id: number;
  nome: string;
  raca: string;
  dataNascimento: string;
  sexo: 'MACHO' | 'FEMEA';
}

// --- Lógica de Rota e Modo de Edição ---
const router = useRouter();
const route = useRoute();
const cachorroId = computed(() => route.params.id as string | undefined);
const isEditMode = computed(() => !!cachorroId.value);

// --- Estado do Componente ---
const cachorroData = ref<CachorroPayload>({
  nome: '',
  raca: '',
  dataNascimento: '',
  sexo: ''
});
const loading = ref(false);
const errorMessage = ref<string | null>(null);
const successMessage = ref<string | null>(null);

// Lógica para buscar os dados ao carregar a página em modo de edição
onMounted(async () => {
  if (isEditMode.value) {
    loading.value = true;
    try {
      const response = await api.get<CachorroResponse>(`/cachorros/${cachorroId.value}`);
      const data = response.data;
      // Preenche o formulário com os dados existentes
      cachorroData.value = {
        nome: data.nome,
        raca: data.raca,
        dataNascimento: data.dataNascimento,
        sexo: data.sexo
      };
    } catch (err) {
      errorMessage.value = 'Falha ao carregar os dados do cachorro para edição.';
      console.error(err);
    } finally {
      loading.value = false;
    }
  }
});

// Função de envio que lida com POST (criação) e PATCH (atualização)
const handleSubmit = async () => {
  loading.value = true;
  errorMessage.value = null;
  successMessage.value = null;

  try {
    let response;
    if (isEditMode.value) {
      // MODO EDIÇÃO: Envia uma requisição PATCH
      // PATCH é ideal para atualizações parciais. O seu backend já suporta isso!
      response = await api.patch(`/cachorros/${cachorroId.value}`, cachorroData.value);
      successMessage.value = `Cão "${response.data.nome}" atualizado com sucesso!`;
    } else {
      // MODO CRIAÇÃO: Envia uma requisição POST
      response = await api.post('/cachorros', cachorroData.value);
      successMessage.value = `Cão "${response.data.nome}" cadastrado com sucesso!`;
    }

    // Redireciona para a página de detalhes do cão após um breve momento
    setTimeout(() => {
      router.push(`/cachorros/${response.data.id}`);
    }, 1500);

  } catch (err) {
    errorMessage.value = `Falha ao ${isEditMode.value ? 'atualizar' : 'cadastrar'} o cachorro. Verifique os dados.`;
    console.error(err);
  } finally {
    // Apenas desliga o loading se não houver sucesso, para o usuário ver a mensagem.
    if (!successMessage.value) {
      loading.value = false;
    }
  }
};
</script>

<style scoped>
.form-view {
  padding: 2rem;
  background-color: #f4f7f6;
}
.container {
  max-width: 700px;
  margin: 2rem auto;
  padding: 2.5rem;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}
header {
  text-align: center;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #e9ecef;
}
header h1 {
  margin: 0;
  color: #2c3e50;
  font-weight: 600;
}
.subtitle {
  font-size: 1rem;
  color: #6c757d;
  margin-top: 0.5rem;
}
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}
.form-group {
  display: flex;
  flex-direction: column;
}
.form-group label {
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #495057;
}
.form-group input,
.form-group select {
  padding: 0.75rem;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 1rem;
}
.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #80bdff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}
.form-actions {
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e9ecef;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}
.btn-primary, .btn-secondary {
  padding: 0.7rem 1.5rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  text-decoration: none;
  font-weight: 500;
  text-align: center;
  transition: background-color 0.2s, transform 0.1s;
}
.btn-primary:hover:not(:disabled) { background-color: #0069d9; transform: translateY(-1px); }
.btn-primary:disabled { background-color: #a0cfff; cursor: not-allowed; }
.btn-secondary { background-color: #6c757d; color: white; }
.btn-secondary:hover { background-color: #5a6268; transform: translateY(-1px); }
.alert {
  margin-top: 1.5rem;
  padding: 1rem;
  border-radius: 5px;
  text-align: center;
}
.alert-success { background-color: #d4edda; color: #155724; }
.alert-danger { background-color: #f8d7da; color: #721c24; }
</style>
