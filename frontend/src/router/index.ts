import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'
import CachorrosView from '../views/CachorrosView.vue'
import TutoresView from '../views/TutoresView.vue'
import CachorroFormView from '../views/CachorroFormView.vue'
import CachorroDetalhesView from '../views/CachorroDetalhesView.vue'
import TutorFormView from '../views/TutorFormView.vue'
import TutorDetalhesView from '../views/TutorDetalhesView.vue'
import NinhadasView from '../views/NinhadasView.vue';
import NinhadaFormView from '../views/NinhadaFormView.vue';
import NinhadaDetalhesView from '../views/NinhadaDetalhesView.vue';


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/dashboard', name: 'dashboard', component: DashboardView },

    // --- Rotas de Cachorros ---
    { path: '/cachorros', name: 'cachorros-lista', component: CachorrosView },
    { path: '/cachorros/:id', name: 'cachorro-detalhes', component: CachorroDetalhesView, props: true },
    { path: '/cachorros/novo', name: 'cachorro-novo', component: CachorroFormView },
    { path: '/cachorros/:id/editar', name: 'cachorro-editar', component: CachorroFormView, props: true },

    // --- Rotas de Tutores ---
    { path: '/tutores', name: 'tutores-lista', component: TutoresView },
    { path: '/tutores/novo', name: 'tutor-novo', component: TutorFormView },
    { path: '/tutores/:id', name: 'tutor-detalhes', component: TutorDetalhesView, props: true },
    { path: '/tutores/:id/editar', name: 'tutor-editar', component: TutorFormView, props: true },

    // --- Rotas de Ninhadas (Nomes Padronizados) ---
    { path: '/ninhadas', name: 'ninhadas-lista', component: NinhadasView },
    { path: '/ninhadas/nova', name: 'ninhada-nova', component: NinhadaFormView },
    { path: '/ninhadas/:id', name: 'ninhada-detalhes', component: NinhadaDetalhesView, props: true } // Adicionado props: true
  ]
})

export default router;
