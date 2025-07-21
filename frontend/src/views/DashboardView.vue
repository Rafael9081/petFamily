<template>
  <div class="dashboard-view">
    <header class="view-header">
      <!-- Container para alinhar título e botão lado a lado -->
      <div class="header-content">
        <!-- Div para agrupar o título e o parágrafo -->
        <div>
          <h1>Dashboard</h1>
          <p>Visão geral e atividades recentes do seu canil.</p>
        </div>
        <!-- O botão agora está alinhado à direita graças ao flexbox no .header-content -->
        <router-link to="/cachorros/novo" class="btn btn-primary">
          Cadastrar Cachorro
        </router-link>
      </div>
    </header>

    <div v-if="loading" class="loading-message">Carregando dados do dashboard...</div>
    <div v-if="error" class="error-message">{{ error }}</div>

    <div v-if="!loading && !error" class="dashboard-content">
      <!-- Cards Resumo Rápido -->
      <section class="quick-stats">
        <div class="stat-box">
          <div class="icon-circle"><i class="icon paw-icon"></i></div>
          <div class="stat-text">
            <div class="stat-number">{{ stats.caesDisponiveis }}</div>
            <div class="stat-label">Cães Disponíveis</div>
          </div>
        </div>
        <div class="stat-box">
          <div class="icon-circle"><i class="icon user-icon"></i></div>
          <div class="stat-text">
            <div class="stat-number">{{ stats.totalTutores }}</div>
            <div class="stat-label">Tutores Cadastrados</div>
          </div>
        </div>
      </section>

      <!-- Resumo Financeiro -->
      <section class="financial-summary">
        <h2>Resumo dos Últimos 30 Dias</h2>
        <div class="finance-boxes">
          <div class="finance-card blue-light">
            <span class="label">Receita</span>
            <span class="value">{{ formatCurrency(financeiro.receita) }}</span>
          </div>
          <div class="finance-card red-light">
            <span class="label">Despesas</span>
            <span class="value">{{ formatCurrency(financeiro.despesa) }}</span>
          </div>
        </div>
        <div class="profit-box" :class="{ 'negative': financeiro.lucro < 0 }">
          <span class="label">Resultado</span>
          <span class="value">{{ formatCurrency(financeiro.lucro) }}</span>
        </div>
      </section>

      <!-- Atividades Recentes -->
      <section class="recent-activities">
        <h2>Atividades Recentes</h2>
        <div v-if="atividades.length > 0" class="activities-list">
          <router-link
            v-for="atividade in atividades"
            :key="atividade.data + atividade.descricao"
            :to="`/cachorros/${atividade.entidadeId}`"
            class="activity-item"
          >
            <span :class="['tag', `tag-${atividade.tipo.toLowerCase()}`]">{{ atividade.tipo }}</span>
            <div class="text">
              <p>{{ atividade.descricao }}</p>
              <span class="date">{{ formatDate(atividade.data) }}</span>
            </div>
          </router-link>
        </div>
        <div v-else class="no-data">Nenhuma atividade recente registrada.</div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import api from '@/service/api';

interface DashboardStats { caesDisponiveis: number; totalTutores: number; }
interface FinanceiroDashboard { receita: number; despesa: number; lucro: number; }
interface AtividadeRecente { tipo: 'VENDA' | 'NINHADA' | 'DESPESA'; descricao: string; data: string; entidadeId: number; }

const loading = ref(true);
const error = ref<string | null>(null);
const stats = ref<DashboardStats>({ caesDisponiveis: 0, totalTutores: 0 });
const financeiro = ref<FinanceiroDashboard>({ receita: 0, despesa: 0, lucro: 0 });
const atividades = ref<AtividadeRecente[]>([]);

const formatCurrency = (value: number) => new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
const formatDate = (isoDate: string) => new Intl.DateTimeFormat('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date(isoDate));

onMounted(async () => {
  try {
    const [statsRes, financeiroRes, atividadesRes] = await Promise.all([
      api.get<DashboardStats>('/dashboard/stats'),
      api.get<FinanceiroDashboard>('/dashboard/financeiro-30-dias'),
      api.get<AtividadeRecente[]>('/dashboard/atividades-recentes')
    ]);
    stats.value = statsRes.data;
    financeiro.value = financeiroRes.data;
    atividades.value = atividadesRes.data;
  } catch (err) {
    console.error("Erro ao carregar dashboard:", err);
    error.value = "Erro ao carregar os dados do dashboard.";
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
/* O CSS que você já tinha, com a adição do .header-content */
.dashboard-view {
  background: #f0f2f5;
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
}

/* NOVO: Container do cabeçalho para alinhar itens */
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem; /* A margem que estava no <p> agora fica aqui */
}

.view-header h1 {
  font-size: 2rem;
  color: #003366;
  font-weight: 700;
  margin-bottom: 0.25rem; /* Pequeno ajuste de espaçamento */
}
.view-header p {
  color: #666;
  margin: 0; /* Remove a margem padrão do parágrafo */
}

/* Estilo para o botão, usando as variáveis globais do main.css */
.btn.btn-primary {
  background-color: var(--color-primary-dark);
  color: white;
  text-decoration: none;
  padding: 0.75rem 1.5rem;
  border-radius: var(--border-radius-button);
  font-weight: 600;
  transition: background-color 0.2s, transform 0.1s;
}
.btn.btn-primary:hover {
  background-color: var(--color-primary-deep);
  transform: translateY(-2px);
}

.quick-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}
.stat-box {
  background: #04376c;
  color: #fff;
  padding: 1.5rem;
  border-radius: 1rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: transform 0.2s ease-in-out;
}
.stat-box:hover {
  transform: translateY(-5px);
}
.icon-circle {
  background: #e63946;
  width: 3.5rem;
  height: 3.5rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-text .stat-number {
  font-size: 2rem;
  font-weight: bold;
}
.stat-label {
  font-size: 1rem;
  opacity: 0.9;
}

.financial-summary {
  background: white;
  padding: 1.5rem;
  border-radius: 1rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}
.financial-summary h2, .recent-activities h2 {
  font-size: 1.2rem;
  font-weight: 600;
  margin-top: 0;
  margin-bottom: 1rem;
  color: #333;
}
.finance-boxes {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1rem;
}
.finance-card {
  padding: 1rem;
  border-radius: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}
.finance-card .label {
  font-weight: bold;
  color: #333;
  font-size: 0.9rem;
}
.finance-card .value {
  font-size: 1.75rem;
  font-weight: 700;
}
.blue-light { background: #d0e7ff; color: #0056b3; }
.red-light { background: #ffd6d6; color: #a30000; }

.profit-box {
  background: #04376c;
  color: white;
  padding: 1rem;
  border-radius: 0.75rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1.2rem;
  font-weight: bold;
}
.profit-box.negative {
  background-color: #c0392b;
}
.profit-box .value {
  font-size: 1.5rem;
}

.recent-activities {
  background: white;
  padding: 1.5rem;
  border-radius: 1rem;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}
.activities-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.activity-item {
  display: flex;
  gap: 1rem;
  padding: 0.75rem;
  border-radius: 0.75rem;
  align-items: center;
  text-decoration: none;
  color: inherit;
  transition: background-color 0.2s;
  border: 1px solid transparent;
}
.activity-item:hover {
  background: #f8f9fa;
  border-color: #e9ecef;
}
.activity-item .tag {
  color: white;
  font-weight: bold;
  padding: 0.25rem 0.6rem;
  border-radius: 1rem;
  font-size: 0.7rem;
  text-transform: uppercase;
  flex-shrink: 0;
}
.tag-venda { background-color: #3498db; }
.tag-ninhada { background-color: #2ecc71; }
.tag-despesa { background-color: #e63946; }

.activity-item .text p {
  margin: 0;
  font-size: 0.95rem;
  font-weight: 500;
}
.activity-item .date {
  font-size: 0.85rem;
  color: #777;
}

.no-data, .loading-message, .error-message {
  text-align: center;
  color: #999;
  font-style: italic;
  padding: 2rem;
}
.error-message { color: #e63946; font-weight: bold; }

/* --- ÍCONES EM CSS PURO --- */
.icon {
  display: inline-block;
  background-color: white;
}
.paw-icon {
  width: 28px;
  height: 26px;
  -webkit-mask: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 512 512'%3E%3Cpath d='M256 512c20.54 0 39.42-8.4 53-22.59l4.38-4.38C330.5 468.4 352 425.2 352 376c0-39.77-27.61-72-64-72s-64 32.23-64 72c0 49.2 21.5 92.41 38.62 109.03l4.38 4.38C216.6 503.6 235.5 512 256 512zM128 440c-17.67 0-32-14.33-32-32 0-35.35 21.5-65.85 48-76.38V320c0-44.18 35.82-80 80-80s80 35.82 80 80v11.62c26.5 10.53 48 41.03 48 76.38 0 17.67-14.33 32-32 32s-32-14.33-32-32c0-17.67-14.33-32-32-32s-32 14.33-32 32c0 17.67-14.33 32-32 32zM424 280c-13.25 0-24-10.75-24-24 0-39.77-27.61-72-64-72s-64 32.23-64 72c0 13.25-10.75 24-24 24s-24-10.75-24-24c0-66.27 53.73-120 120-120s120 53.73 120 120c0 13.25-10.75 24-24 24zM120 160c-22.09 0-40 17.91-40 40 0 13.25-10.75 24-24 24S32 213.3 32 200c0-48.6 39.4-88 88-88s88 39.4 88 88c0 13.25-10.75 24-24 24s-24-10.75-24-40z'/%3E%3C/svg%3E") no-repeat center;
  mask: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 512 512'%3E%3Cpath d='M256 512c20.54 0 39.42-8.4 53-22.59l4.38-4.38C330.5 468.4 352 425.2 352 376c0-39.77-27.61-72-64-72s-64 32.23-64 72c0 49.2 21.5 92.41 38.62 109.03l4.38 4.38C216.6 503.6 235.5 512 256 512zM128 440c-17.67 0-32-14.33-32-32 0-35.35 21.5-65.85 48-76.38V320c0-44.18 35.82-80 80-80s80 35.82 80 80v11.62c26.5 10.53 48 41.03 48 76.38 0 17.67-14.33 32-32 32s-32-14.33-32-32c0-17.67-14.33-32-32-32s-32 14.33-32 32c0 17.67-14.33 32-32 32zM424 280c-13.25 0-24-10.75-24-24 0-39.77-27.61-72-64-72s-64 32.23-64 72c0 13.25-10.75 24-24 24s-24-10.75-24-24c0-66.27 53.73-120 120-120s120 53.73 120 120c0 13.25-10.75 24-24 24zM120 160c-22.09 0-40 17.91-40 40 0 13.25-10.75 24-24 24S32 213.3 32 200c0-48.6 39.4-88 88-88s88 39.4 88 88c0 13.25-10.75 24-24 24s-24-10.75-24-40z'/%3E%3C/svg%3E") no-repeat center;
}
.user-icon {
  width: 28px;
  height: 28px;
  -webkit-mask: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 448 512'%3E%3Cpath d='M224 256c70.7 0 128-57.3 128-128S294.7 0 224 0 96 57.3 96 128s57.3 128 128 128zm-45.7 48C79.8 304 0 383.8 0 482.3 0 498.7 13.3 512 29.7 512h388.6c16.4 0 29.7-13.3 29.7-29.7C448 383.8 368.2 304 269.7 304H178.3z'/%3E%3C/svg%3E") no-repeat center;
  mask: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 448 512'%3E%3Cpath d='M224 256c70.7 0 128-57.3 128-128S294.7 0 224 0 96 57.3 96 128s57.3 128 128 128zm-45.7 48C79.8 304 0 383.8 0 482.3 0 498.7 13.3 512 29.7 512h388.6c16.4 0 29.7-13.3 29.7-29.7C448 383.8 368.2 304 269.7 304H178.3z'/%3E%3C/svg%3E") no-repeat center;
}
</style>
