<template>
  <aside class="sidebar" :class="{ 'is-open': isOpen }">
    <div class="sidebar-header">
      <h2 class="sidebar-title">Canil Manager</h2>
    </div>

    <!-- Navegação Principal -->
    <nav class="sidebar-nav">
      <router-link to="/dashboard" class="nav-link" @click="$emit('close')">
        <span class="icon">📊</span> Dashboard
      </router-link>
      <router-link to="/cachorros" class="nav-link" @click="$emit('close')">
        <span class="icon">🐾</span> Cachorros
      </router-link>
      <router-link to="/tutores" class="nav-link" @click="$emit('close')">
        <span class="icon">👥</span> Tutores
      </router-link>
      <router-link to="/ninhadas" class="nav-link" @click="$emit('close')">
        <span class="icon">🐣</span> Ninhadas
      </router-link>
    </nav>

    <!-- Ação Rápida no Rodapé do Menu -->
    <div class="sidebar-footer">
      <router-link to="/ninhadas/nova" class="btn-action" @click="$emit('close')">
        <span class="icon">+</span> Registrar Ninhada
      </router-link>
    </div>
  </aside>
</template>

<script setup lang="ts">
// Este componente recebe uma propriedade 'isOpen' do componente pai (App.vue)
// para saber se deve estar visível ou não.
defineProps<{
  isOpen: boolean;
}>();

// Define que este componente pode emitir um evento 'close' para o pai.
// Usamos isso para fechar o menu ao clicar em um link.
defineEmits(['close']);
</script>

<style scoped>
.sidebar {
  position: fixed;
  top: 0;
  left: -260px; /* Inicia fora da tela */
  width: 250px;
  height: 100vh;
  z-index: 1000;
  background-color: #2c3e50;
  color: #ecf0f1;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.2);
  transition: left 0.3s ease-in-out;
  display: flex;
  flex-direction: column;
}

.sidebar.is-open {
  left: 0; /* Entra na tela */
}

.sidebar-header {
  padding: 1.5rem 1rem;
  text-align: center;
  border-bottom: 1px solid #34495e;
}

.sidebar-title {
  margin: 0;
  font-size: 1.5rem;
  color: #ffffff;
  font-weight: 600;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  padding-top: 1rem;
  flex-grow: 1; /* Faz a navegação ocupar o espaço disponível, empurrando o rodapé para baixo */
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 1.5rem;
  color: #bdc3c7;
  text-decoration: none;
  font-size: 1.1rem;
  transition: background-color 0.2s, color 0.2s;
  border-left: 4px solid transparent;
}

.nav-link:hover {
  background-color: #34495e;
  color: #ffffff;
}

/* Estilo para o link ativo */
.nav-link.router-link-exact-active {
  background-color: #34495e;
  color: #ffffff;
  font-weight: 600;
  border-left-color: #3498db;
}

.icon {
  font-size: 1.2rem;
  width: 25px;
  text-align: center;
}

/* --- NOVOS ESTILOS PARA O BOTÃO DE AÇÃO --- */
.sidebar-footer {
  padding: 1rem 1.5rem;
  margin-top: auto; /* Garante que o rodapé fique no final do menu */
  border-top: 1px solid #34495e;
}

.btn-action {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  width: 100%;
  padding: 0.75rem;
  background-color: #3498db;
  color: #ffffff;
  border-radius: 5px;
  text-decoration: none;
  font-weight: 600;
  text-align: center;
  transition: background-color 0.2s;
}

.btn-action:hover {
  background-color: #2980b9;
}

.btn-action .icon {
  font-size: 1.1rem;
}
</style>
