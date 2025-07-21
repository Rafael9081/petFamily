<template>
  <div class="detalhes-view">
    <div v-if="loading" class="loading-message">Carregando...</div>
    <div v-if="error" class="error-message">{{ error }}</div>

    <div v-if="cachorro" class="container">
      <!-- Estrutura de cabeçalho padronizada -->
      <header class="view-header">
        <div class="header-main">
          <h1>{{ cachorro.nome }}</h1>
          <span :class="['status-tag', cachorro.status.toLowerCase()]">{{ cachorro.status }}</span>
        </div>
        <p>Detalhes completos, histórico financeiro e ações.</p>
        <router-link :to="`/cachorros/${cachorro.id}/editar`" class="btn-editar">
          Editar Informações
        </router-link>
      </header>

      <!-- Grid de informações principais -->
      <div class="info-grid">
        <!-- Cada seção de informação agora é um card padronizado -->
        <section class="card">
          <h3>Informações Básicas</h3>
          <ul class="info-list">
            <li><strong>Raça:</strong> {{ cachorro.raca }}</li>
            <li><strong>Sexo:</strong> {{ cachorro.sexo }}</li>
            <li><strong>Nascimento:</strong> {{ formatarData(cachorro.dataNascimento) }}</li>
            <li><strong>Idade:</strong> {{ calcularIdade(cachorro.dataNascimento) }}</li>
            <li><strong>Tutor:</strong> {{ cachorro.tutor?.nome || 'Sem tutor' }}</li>
          </ul>
        </section>

        <section v-if="relatorioFinanceiro" class="card">
          <h3>Financeiro</h3>
          <ul class="info-list">
            <li><strong>Custo Total:</strong> {{ formatarMoeda(relatorioFinanceiro.custoTotal) }}</li>
            <li v-if="cachorro.status === 'VENDIDO' && relatorioFinanceiro.registroVenda">
              <strong>Valor da Venda:</strong> {{ formatarMoeda(relatorioFinanceiro.registroVenda.valor) }}
            </li>
            <li v-if="cachorro.status === 'VENDIDO'" :class="relatorioFinanceiro.lucro >= 0 ? 'lucro' : 'prejuizo'">
              <strong>Resultado:</strong> {{ formatarMoeda(relatorioFinanceiro.lucro) }}
            </li>
          </ul>
        </section>
      </div>

      <!-- Seção de Venda -->
      <section class="card">
        <div v-if="cachorro.status === 'DISPONIVEL'">
          <VenderCachorroForm :cachorro-id="cachorro.id" @venda-concluida="handleVendaConcluida" />
        </div>
        <div v-else class="status-info-box">
          <p>Este cachorro não está disponível para venda. (Status: {{ cachorro.status }})</p>
        </div>
      </section>

      <!-- Histórico de despesas -->
      <section v-if="relatorioFinanceiro && relatorioFinanceiro.historicoDespesas.length > 0" class="card">
        <h3>Histórico de Despesas</h3>
        <ul class="despesas-list">
          <li v-for="despesa in relatorioFinanceiro.historicoDespesas" :key="despesa.id">
            <span>{{ formatarData(despesa.data) }}</span>
            <span>{{ despesa.descricao }}</span>
            <span class="valor-despesa">{{ formatarMoeda(despesa.valor) }}</span>
          </li>
        </ul>
      </section>

      <!-- Seção de ninhadas -->
      <section v-if="ninhadas.length > 0" class="card">
        <h3>Ninhadas (como {{ cachorro.sexo === 'FEMEA' ? 'Mãe' : 'Pai' }})</h3>
        <ul class="ninhadas-list">
          <li v-for="ninhada in ninhadas" :key="ninhada.id">
            <span>Nascimento: <strong>{{ formatarData(ninhada.dataNascimento) }}</strong></span>
            <span>Com: <strong>{{ ninhada.pai?.nome || ninhada.mae?.nome || 'Desconhecido' }}</strong></span>
            <span>Nº de Filhotes: <strong>{{ ninhada.totalFilhotes }}</strong></span>
          </li>
        </ul>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
// O SCRIPT CONTINUA EXATAMENTE O MESMO, NENHUMA MUDANÇA NA LÓGICA!
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import cachorroService from '@/service/CachorroService';
import VenderCachorroForm from '@/components/VenderCachorroForm.vue';
import { type VendaResponse } from '@/service/VendaService';

interface Tutor { id: number; nome: string; }
interface Despesa { id: number; descricao: string; valor: number; data: string; }
interface Venda { id: number; valor: number; data: string; }
interface Ninhada { id: number; dataNascimento: string; pai?: { nome: string }; mae?: { nome: string }; totalFilhotes: number; }
interface RelatorioFinanceiro { custoTotal: number; registroVenda?: Venda; lucro: number; historicoDespesas: Despesa[]; }
interface Cachorro {
  id: number;
  nome: string;
  raca: string;
  dataNascimento: string;
  sexo: 'MACHO' | 'FEMEA';
  status: 'DISPONIVEL' | 'RESERVADO' | 'VENDIDO' | 'MATRIZ_PADREADOR' | 'INDISPONIVEL';
  tutor: Tutor | null;
}

const route = useRoute();
const cachorro = ref<Cachorro | null>(null);
const relatorioFinanceiro = ref<RelatorioFinanceiro | null>(null);
const ninhadas = ref<Ninhada[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

const formatarData = (dataString: string) => new Date(dataString).toLocaleDateString('pt-BR', { timeZone: 'UTC' });
const formatarMoeda = (valor: number) => (valor || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
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
  return idadeAnos > 0 ? `${idadeAnos} ano(s)` : `${idadeMeses} mese(s)`;
};

onMounted(async () => {
  const cachorroId = Number(route.params.id);
  if (isNaN(cachorroId)) {
    error.value = "ID do cachorro inválido.";
    loading.value = false;
    return;
  }
  try {
    const [cachorroResponse, financeiroResponse, ninhadasResponse] = await Promise.all([
      cachorroService.buscarDetalhes(cachorroId),
      cachorroService.buscarRelatorioFinanceiro(cachorroId),
      cachorroService.listarNinhadasDoCachorro(cachorroId),
    ]);
    cachorro.value = cachorroResponse.data;
    relatorioFinanceiro.value = financeiroResponse.data;
    ninhadas.value = ninhadasResponse.data;
  } catch (err: any) {
    error.value = "Não foi possível carregar os dados do cachorro.";
    console.error("Erro ao buscar detalhes do cachorro:", err);
  } finally {
    loading.value = false;
  }
});

function handleVendaConcluida(venda: VendaResponse) {
  if (cachorro.value) {
    cachorro.value.status = 'VENDIDO';
    cachorro.value.tutor = venda.tutor;
    cachorroService.buscarRelatorioFinanceiro(cachorro.value.id)
      .then(response => {
        relatorioFinanceiro.value = response.data;
      });
  }
}
</script>

<style scoped>
/*
  A MÁGICA: A maioria dos estilos foi para o main.css.
  Aqui ficam apenas os estilos que são 100% específicos desta página.
*/
.detalhes-view {
  padding: 2rem;
}
.container {
  max-width: 1200px;
  margin: 0 auto;
}

.header-main {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.btn-editar {
  background-color: var(--color-primary-dark);
  color: white;
  padding: 0.6rem 1.2rem;
  border-radius: var(--border-radius-button);
  text-decoration: none;
  font-weight: 600;
  transition: background-color 0.2s;
  display: inline-block;
  margin-top: 1rem;
}
.btn-editar:hover {
  background-color: var(--color-primary-deep);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.info-list, .despesas-list, .ninhadas-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.info-list li, .despesas-list li, .ninhadas-list li {
  display: flex;
  justify-content: space-between;
  padding: 0.75rem 0;
  border-bottom: 1px solid var(--color-border);
}
.info-list li:last-child, .despesas-list li:last-child, .ninhadas-list li:last-child {
  border-bottom: none;
}
.lucro { color: var(--color-success); }
.prejuizo { color: var(--color-danger); }
.valor-despesa { font-weight: 600; }

.status-info-box {
  background-color: #e9ecef;
  border-left: 5px solid #6c757d;
  padding: 1rem 1.5rem;
  border-radius: 8px;
  text-align: center;
  font-weight: 500;
}

.status-tag {
  display: inline-block;
  padding: 0.3em 0.8em;
  font-size: 0.85rem;
  font-weight: 700;
  line-height: 1;
  text-align: center;
  white-space: nowrap;
  vertical-align: baseline;
  border-radius: 50px; /* Plula */
  color: #fff;
}
.status-tag.disponivel { background-color: var(--color-accent-green); }
.status-tag.vendido { background-color: var(--color-text-secondary); }
.status-tag.reservado { background-color: #ffc107; color: #212529; }
.status-tag.matriz_padreador { background-color: var(--color-accent-blue); }
.status-tag.indisponivel { background-color: var(--color-accent-red); }
</style>
