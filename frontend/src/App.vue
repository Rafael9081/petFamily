<template>
  <div id="app-container" :class="{ 'sidebar-open': isSidebarOpen }">
    <!-- 1. Carrega o componente Sidebar, passando o estado e ouvindo o evento de fechar -->
    <Sidebar :isOpen="isSidebarOpen" @close="closeSidebar" />

    <!-- 2. Overlay que escurece o fundo e fecha o menu ao ser clicado -->
    <div v-if="isSidebarOpen" class="sidebar-overlay" @click="closeSidebar"></div>

    <div class="main-content">
      <header class="main-header">
        <!-- 3. Botão "Hambúrguer" que controla a visibilidade do menu -->
        <button @click="toggleSidebar" class="hamburger-button">
          ☰
        </button>
        <!-- Outros elementos do cabeçalho, como perfil do usu podem ir aqui -->
      </header>

      <main class="page-content">
        <!-- 4. O coração da navegação: onde o Vue Router renderiza a página atual -->
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import Sidebar from '@/components/SideBar.vue'; // Importação do componente de menu

// Variável reativa que controla se o menu está aberto ou fechado.
const isSidebarOpen = ref(false);

// Funara alternar o estado do menu.
const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value;
};

// Função para garantir que o menu seja fechado.
const closeSidebar = () => {
  isSidebarOpen.value = false;
};
</script>

<style>
/* Estilos Globais para a aplicação */
:root {
  --sidebar-width: 250px;
  --header-height: 60px;
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f4f7f6;
}

#app-container {
  transition: transform 0.3s ease-in-out;
}

/* Estrutura do Layout Principal */
.main-content {
  padding-top: var(--header-height); /* Espaço para o header fixo */
  transition: margin-left 0.3s ease-in-out;
}

.main-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: var(--header-height);
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  padding: 0 1rem;
  z-index: 900;
}

.hamburger-button {
  background: none;
  border: none;
  font-size: 1.8rem;
  cursor: pointer;
  color: #2c3e50;
}

.page-content {
  padding: 1rem; /* Um preenchimento padrão para o conteúdo das páginas */
}

/* Overlay que aparece quando o menu está aberto */
.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999; /* Fica entre o menu e o conteúdo */
  transition: opacity 0.3s ease-in-out;
}

/* Para telas maiores, o menu empurra o conteúdo para o lado */
@media (min-width: 768px) {
  .sidebar-open .main-content {
    margin-left: var(--sidebar-width);
  }
}
</style>
