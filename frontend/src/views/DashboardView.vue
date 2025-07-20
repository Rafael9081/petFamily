<template>
  <div class="dashboard-view">
    <header class="view-header">
      <h1>Dashboard</h1>
      <p>Visão geral e atividades recentes do seu canil.</p>
    </header>

    <div v-if="loading" class="loading-message">Carregando dados do dashboard...</div>
    <div v-if="error" class="error-message">{{ error }}</div>

    <div v-if="!loading && !error" class="dashboard-content">
      <!-- Seção 1: Cards de Resumo Rápido -->
      <section class="stats-grid">
        <div class="stat-card">
          <div class="card-icon dogs-icon">🐾</div>
          <div class="card-content">
            <span class="card-value">{{ stats.caesDisponiveis }}</span>
            <span class="card-label">Cães Disponíveis</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="card-icon users-icon">👥</div>
          <div class="card-content">
            <span class="card-value">{{ stats.totalTutores }}</span>
            <span class="card-label">Tutores Cadastrados</span>
          </div>
        </div>
      </section>

      <!-- Seção 2: Resumo Financeiro -->
      <section class="finance-summary">
        <h2>Resumo dos Últimos 30 Dias</h2>
        <div class="finance-cards">
          <div class="finance-card revenue">
            <span class="card-label">Receita</span>
            <span class="card-value">{{ formatCurrency(financeiro.receita) }}</span>
          </div>
          <div class="finance-card expense">
            <span class="card-label">Despesas</span>
            <span class="card-value">{{ formatCurrency(financeiro.despesa) }}</span>
          </div>
          <div class="finance-card profit">
            <span class="card-label">Lucro</span>
            <span class="card-value" :class="{ 'negative': financeiro.lucro < 0 }">
              {{ formatCurrency(financeiro.lucro) }}
            </span>
          </div>
        </div>
      </section>

      <!-- Seção 3: Feed de Atividades Recentes com Links -->
      <section class="activity-feed">
        <h2>Atividades Recentes</h2>
        <ul v-if="atividades.length > 0">
          <router-link
            v-for="atividade in atividades"
            :key="atividade.data + atividade.descricao"
            :to="`/cachorros/${atividade.entidadeId}`"
            tag="li"
            class="activity-item"
          >
            <span :class="['activity-tag', `tag-${atividade.tipo.toLowerCase()}`]">{{ atividade.tipo }}</span>
            <span class="activity-description">{{ atividade.descricao }}</span>
            <span class="activity-date">{{ formatDate(atividade.data) }}</span>
          </router-link>
        </ul>
        <div v-else class="no-data">Nenhuma atividade recente registrada.</div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import api from '@/service/api'; // Certifique-se que este é o caminho para sua instância do Axios

// --- Interfaces para os dados do Dashboard ---
interface DashboardStats {
  caesDisponiveis: number;
  totalTutores: number;
}
interface FinanceiroDashboard {
  receita: number;
  despesa: number;
  lucro: number;
}
interface AtividadeRecente {
  tipo: 'VENDA' | 'NINHADA' | 'DESPESA';
  descricao: string;
  data: string; // Vem como string ISO (ex: "2024-07-16T14:30:00")
  entidadeId: number;
}

// --- Estado do Componente ---
const loading = ref(true);
const error = ref<string | null>(null);

const stats = ref<DashboardStats>({ caesDisponiveis: 0, totalTutores: 0 });
const financeiro = ref<FinanceiroDashboard>({ receita: 0, despesa: 0, lucro: 0 });
const atividades = ref<AtividadeRecente[]>([]);

// --- Funções de Formatação ---
const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
};

const formatDate = (isoDate: string) => {
  const date = new Date(isoDate);
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(date);
};

// --- LÓGICA DE BUSCA DE DADOS ---
// onMounted é um "gancho de ciclo de vida" que executa este código
// assim que o componente é renderizado na tela.
onMounted(async () => {
  try {
    // Promise.all executa todas as chamadas à API ao mesmo tempo.
    // É muito mais rápido do que fazer uma de cada vez.
    const [statsRes, financeiroRes, atividadesRes] = await Promise.all([
      api.get<DashboardStats>('/dashboard/stats'),
      api.get<FinanceiroDashboard>('/dashboard/financeiro-30-dias'),
      api.get<AtividadeRecente[]>('/dashboard/atividades-recentes')
    ]);

    // Preenchemos nossos dados com as respostas da API
    stats.value = statsRes.data;
    financeiro.value = financeiroRes.data;
    atividades.value = atividadesRes.data;

  } catch (err) {
    console.error("Falha ao carregar dados do dashboard:", err);
    error.value = "Não foi possível carregar os dados do dashboard. Tente novamente mais tarde.";
  } finally {
    // Independentemente de sucesso ou falha, paramos de carregar.
    loading.value = false;
  }
});
</script>

<style scoped>
/* ESTILO GERAL: Fundo claro, fonte escura e legível */
.dashboard-view {
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
  background-color: #f8f9fa; /* Fundo bem claro, quase branco */
  color: #212529; /* Texto principal bem escuro */
}

.view-header {
  margin-bottom: 2rem;
}

.view-header h1 {
  font-weight: 600;
  font-size: 2rem;
  color: #343a40;
}

.view-header p {
  color: #6c757d;
  font-size: 1.1rem;
}

/* CARDS DE RESUMO RÁPIDO */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2.5rem;
}

.stat-card {
  background-color: #ffffff;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.card-icon {
  font-size: 2.5rem;
  padding: 0.8rem;
  border-radius: 50%;
  color: #fff;
}
.dogs-icon { background-color: #0d6efd; }
.users-icon { background-color: #198754; }

.card-content {
  display: flex;
  flex-direction: column;
}

.card-value {
  font-size: 2.2rem;
  font-weight: 700;
  color: #212529;
}

.card-label {
  font-size: 0.9rem;
  color: #6c757d;
}

/* RESUMO FINANCEIRO */
.finance-summary, .activity-feed {
  background-color: #ffffff;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2.5rem;
}

h2 {
  font-size: 1.4rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #343a40;
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 0.75rem;
}

.finance-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.finance-card {
  padding: 1rem;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  text-align: center;
}
.finance-card .card-label {
  font-weight: 500;
}
.finance-card .card-value {
  font-size: 1.8rem;
  font-weight: 700;
}

.revenue { background-color: #d4edda; color: #155724; }
.expense { background-color: #f8d7da; color: #721c24; }
.profit { background-color: #cfe2ff; color: #084298; }
.profit .card-value.negative { color: #842029; }

/* FEED DE ATIVIDADES */
.activity-feed ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border-bottom: 1px solid #f1f3f5;
  text-decoration: none;
  color: inherit;
  transition: background-color 0.2s;
  border-radius: 6px;
  cursor: pointer;
}
.activity-item:hover {
  background-color: #e9ecef;
}
.activity-item:last-child {
  border-bottom: none;
}

.activity-tag {
  font-size: 0.75rem;
  font-weight: 700;
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  color: #fff;
  flex-shrink: 0;
}
.tag-venda { background-color: #0d6efd; }
.tag-ninhada { background-color: #198754; }
.tag-despesa { background-color: #dc3545; }

.activity-description {
  flex-grow: 1;
  color: #495057;
}

.activity-date {
  font-size: 0.9rem;
  color: #6c757d;
  flex-shrink: 0;
}

/* MENSAGENS DE ESTADO */
.no-data, .loading-message, .error-message {
  text-align: center;
  padding: 2rem;
  color: #6c757d;
  font-style: italic;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}
.error-message {
  color: #d32f2f;
  background-color: #f8d7da;
  border-color: #f5c6cb;
}
</style>
