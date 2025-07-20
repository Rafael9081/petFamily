<template>
  <div class="detalhes-view">
    <div v-if="loading" class="loading-message">Carregando dados do pet...</div>
    <div v-if="error" class="error-message">{{ error }}</div>

    <!-- Container principal que só renderiza quando o 'cachorro' já foi carregado -->
    <div v-if="cachorro" class="container">
      <header class="header">
        <h1>{{ cachorro.nome }}</h1>
        <span class="raca">{{ cachorro.raca }}</span>
        <router-link :to="`/cachorros/${cachorro.id}/editar`" class="btn-editar">
          Editar
        </router-link>
      </header>

      <section class="info-grid">
        <div class="info-card">
          <h3>Informações Básicas</h3>
          <ul>
            <li><strong>Sexo:</strong> {{ cachorro.sexo }}</li>
            <li><strong>Nascimento:</strong> {{ formatarData(cachorro.dataNascimento) }}</li>
            <li><strong>Idade:</strong> {{ calcularIdade(cachorro.dataNascimento) }}</li>
            <!-- Usando optional chaining (?.) para evitar erro se o tutor for nulo -->
            <li><strong>Tutor:</strong> {{ cachorro.tutor?.nome || 'Não definido' }}</li>
          </ul>
        </div>

        <!-- Card financeiro que só renderiza se 'relatorioFinanceiro' existir -->
        <div v-if="relatorioFinanceiro" class="info-card">
          <h3>Financeiro</h3>
          <ul>
            <li><strong>Custo Total:</strong> {{ formatarMoeda(relatorioFinanceiro.custoTotal) }}</li>
            <!-- Verifica se o cão foi vendido e se há um registro de venda -->
            <li v-if="cachorro.foiVendido && relatorioFinanceiro.registroVenda">
              <strong>Valor da Venda:</strong> {{ formatarMoeda(relatorioFinanceiro.registroVenda.valor) }}
            </li>
            <li v-if="cachorro.foiVendido" :class="relatorioFinanceiro.lucro >= 0 ? 'lucro' : 'prejuizo'">
              <strong>Resultado:</strong> {{ formatarMoeda(relatorioFinanceiro.lucro) }}
            </li>
            <li v-else><strong>Status:</strong> Em estoque</li>
          </ul>
        </div>
      </section>

      <!-- Histórico de despesas que só renderiza se houver dados -->
      <section v-if="relatorioFinanceiro && relatorioFinanceiro.historicoDespesas.length > 0" class="info-card">
        <h3>Histórico de Despesas</h3>
        <ul class="despesas-list">
          <li v-for="despesa in relatorioFinanceiro.historicoDespesas" :key="despesa.id">
            <span>{{ formatarData(despesa.data) }}</span>
            <span>{{ despesa.descricao }}</span>
            <span class="valor-despesa">{{ formatarMoeda(despesa.valor) }}</span>
          </li>
        </ul>
      </section>

      <!-- Seção de ninhadas que só renderiza se houver dados -->
      <section v-if="ninhadas.length > 0" class="info-card">
        <!-- Título dinâmico para maior clareza -->
        <h3>Ninhadas (como {{ cachorro.sexo === 'FEMEA' ? 'Mãe' : 'Pai' }})</h3>
        <ul class="ninhadas-list">
          <li v-for="ninhada in ninhadas" :key="ninhada.id">
            <span>Nascimento: <strong>{{ formatarData(ninhada.dataNascimento) }}</strong></span>
            <!-- Nome do parceiro (pai ou m-->
            <span>Com: <strong>{{ ninhada.pai?.nome || ninhada.mae?.nome || 'Não registrado' }}</strong></span>
            <span>Total de Filhotes: <strong>{{ ninhada.totalFilhotes }}</strong> ({{ ninhada.quantidadeMachos }} M / {{ ninhada.quantidadeFemeas }} F)</span>
          </li>
        </ul>
      </section>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import api from '@/service/api';

// --- Interfaces para Tipagem dos Dados ---
interface Tutor { id: number; nome: string; }
interface Despesa { id: number; descricao: string; valor: number; data: string; }
interface Venda { id: number; valor: number; data: string; }
// Adicionei 'mae' para cobrir todos os cenários
interface Ninhada { id: number; dataNascimento: string; pai?: { nome: string }; mae?: { nome: string }; totalFilhotes: number; quantidadeMachos: number; quantidadeFemeas: number; }
interface RelatorioFinanceiro { custoTotal: number; registroVenda?: Venda; lucro: number; historicoDespesas: Despesa[]; }
interface Cachorro {
  id: number;
  nome: string;
  raca: string;
  dataNascimento: string;
  sexo: string;
  foiVendido: boolean;
  tutor: Tutor;
}

// --- Estado do Componente ---
const route = useRoute();
const cachorro = ref<Cachorro | null>(null);
const relatorioFinanceiro = ref<RelatorioFinanceiro | null>(null);
const ninhadas = ref<Ninhada[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

// --- Funções de Formatação ---
const formatarData = (dataString: string) => new Date(dataString).toLocaleDateString('pt-BR', { timeZone: 'UTC' });
const formatarMoeda = (valor: number) => (valor || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

// CORREÇÃO: Função de cálculo de idade padronizada
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

// --- Lógica para Buscar os Dados ---
onMounted(async () => {
  const cachorroId = route.params.id;
  try {
    const [
      cachorroResponse,
      financeiroResponse,
      ninhadasResponse
    ] = await Promise.all([
      api.get(`/cachorros/${cachorroId}`),
      api.get(`/cachorros/${cachorroId}/relatorio-financeiro`),
      api.get(`/cachorros/${cachorroId}/ninhadas`)
    ]);

    cachorro.value = cachorroResponse.data;
    relatorioFinanceiro.value = financeiroResponse.data;
    ninhadas.value = ninhadasResponse.data;

  } catch (err: any) {
    if (err.response && err.response.status === 404) {
      error.value = `Cachorro com ID ${cachorroId} não encontrado.`;
    } else {
      error.value = "Não foi possível carregar os dados do cachorro.";
    }
    console.error("Erro ao buscar detalhes do cachorro:", err);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.detalhes-view {
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
  background-color: #f4f7f6;
}
.container {
  max-width: 900px;
  margin: 0 auto;
}
.header {
  background-color: #007bff;
  color: white;
  padding: 2rem;
  border-radius: 8px;
  margin-bottom: 2rem;
  text-align: center;
}
.header h1 {
  margin: 0;
  font-size: 2.5rem;
}
.header .raca {
  font-size: 1.2rem;
  opacity: 0.9;
}
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}
.info-card {
  background-color: #fff;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  color: #333;
}
.info-card h3 {
  margin-top: 0;
  border-bottom: 2px solid #eee;
  padding-bottom: 0.5rem;
  margin-bottom: 1rem;
}
.info-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.info-card li {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f0f0f0;
}
.info-card li:last-child {
  border-bottom: none;
}
.lucro { color: #28a745; font-weight: bold; }
.prejuizo { color: #dc3545; font-weight: bold; }
.despesas-list li, .ninhadas-list li {
  display: grid;
  /* Ajustado para melhor alinhamento */
  grid-template-columns: 120px 1fr auto;
  gap: 1rem;
  align-items: center;
}
.valor-despesa {
  font-weight: bold;
  text-align: right;
}
.loading-message, .error-message {
  text-align: center;
  padding: 3rem;
  font-size: 1.2rem;
}
.error-message {
  color: #d32f2f;
}
</style>
