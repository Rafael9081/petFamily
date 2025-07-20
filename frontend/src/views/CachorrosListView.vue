<template>
  <div class="list-view">
    <header class="view-header">
      <h1>Nossos Cães</h1>
      <router-link to="/cachorros/novo" class="btn-primary">
        Adicionar Novo Cão
      </router-link>
    </header>

    <div v-if="loading" class="loading-message">Carregando cães...</div>
    <div v-if="error" class="error-message">{{ error }}</div>

    <table v-if="cachorros.length > 0" class="data-table">
      <thead>
      <tr>
        <th>Nome</th>
        <th>Raça</th>
        <th>Idade</th>
        <th>Status</th>
        <th>Ações</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="cachorro in cachorros" :key="cachorro.id">
        <td>{{ cachorro.nome }}</td>
        <td>{{ cachorro.raca }}</td>
        <td>{{ calcularIdade(cachorro.dataNascimento) }}</td>
        <td>
          <!-- CORREÇÃO 1: Classe e texto do status atualizados -->
          <span :class="['status-badge', cachorro.foiVendido ? 'vendido' : 'disponivel']">
              {{ cachorro.foiVendido ? 'Vendido' : 'Disponível para Venda' }}
            </span>
        </td>
        <td class="acoes">
          <router-link :to="`/cachorros/${cachorro.id}`" class="btn-secondary">
            Ver Detalhes
          </router-link>
        </td>
      </tr>
      </tbody>
    </table>

    <div v-else-if="!loading && !error" class="no-data">
      Nenhum cão cadastrado ainda.
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import api from '@/service/api';

interface Cachorro {
  id: number;
  nome: string;
  raca: string;
  dataNascimento: string;
  foiVendido: boolean;
}

interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

const cachorros = ref<Cachorro[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

const calcularIdade = (dataNascimento: string): string => {
  if (!dataNascimento) return 'Idade desconhecida';
  const hoje = new Date();
  const nasc = new Date(dataNascimento);
  let idadeAnos = hoje.getFullYear() - nasc.getFullYear();
  let idadeMeses = hoje.getMonth() - nasc.getMonth();
  if (idadeMeses < 0 || (idadeMeses === 0 && hoje.getDate() < nasc.getDate())) {
    idadeAnos--;
    idadeMeses += 12;
  }
  if (idadeAnos > 0) {
    return `${idadeAnos} ano(s)`;
  }
  return `${idadeMeses} mese(s)`;
};

onMounted(async () => {
  try {
    const response = await api.get<Page<Cachorro>>('/cachorros');
    cachorros.value = response.data.content;
  } catch (err) {
    error.value = 'Falha ao carregar a lista de cães.';
    console.error(err);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.list-view {
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
  background-color: #f8f9fa;
}

.view-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.view-header h1 {
  font-weight: 600;
  color: #343a40;
}

/* --- NOVA ABORDAGEM: Estilo minimalista e focado em tipografia --- */
.data-table {
  width: 100%;
  border-collapse: collapse; /* Essencial para as bordas funcionarem bem */
  /* Removemos o estilo de "caixa" da tabela para um visual mais limpo */
  background-color: transparent;
  box-shadow: none;
  border: none;
}

.data-table th, .data-table td {
  padding: 1.25rem 1rem; /* Mais espaçamento vertical para um look "arejado" */
  text-align: left;
  /* A única borda visível é a que separa as linhas */
  border-bottom: 1px solid #e9ecef;
  color: #212529;
}

.data-table th {
  color: #212529; /* Texto do cabeçalho bem escuro e forte */
  font-size: 0.9rem;
  font-weight: 700; /* Mais peso na fonte para destaque */
  text-transform: none; /* Remove o ALL CAPS para um visual mais sóbrio e legível */
  letter-spacing: normal;
  /* Borda inferior do cabeçalho mais grossa para demarcar a área */
  border-bottom-width: 2px;
  border-bottom-color: #343a40;
  /* Remove o fundo para integrar o cabeçalho ao resto do layout */
  background-color: transparent;
}

.data-table tbody tr:hover {
  background-color: #f1f3f5; /* Mantém um hover sutil para feedback */
}

.status-badge {
  padding: 0.4rem 0.8rem;
  border-radius: 15px;
  font-size: 0.75rem;
  font-weight: bold;
  color: #fff;
  white-space: nowrap; /* Impede a quebra de linha */
}

.status-badge.disponivel { background-color: #198754; }
.status-badge.vendido { background-color: #6c757d; }

.acoes {
  text-align: right;
}

.btn-primary, .btn-secondary {
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  text-decoration: none;
  display: inline-block;
  font-size: 0.9rem;
  border: none;
  transition: all 0.2s ease-in-out;
}

.btn-primary {
  background-color: #0d6efd;
  color: white;
}
.btn-primary:hover {
  background-color: #0b5ed7;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}
.btn-secondary:hover {
  background-color: #5a6268;
}

.no-data, .loading-message, .error-message {
  text-align: center;
  padding: 2rem;
  color: #777;
  font-style: italic;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}
.error-message {
  color: #d32f2f;
}
</style>
