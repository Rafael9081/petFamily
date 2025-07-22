import axios from 'axios';

// 1. Leia a variável de ambiente VITE_API_BASE_URL.
//    O Vite expõe as variáveis de ambiente através de `import.meta.env`.
// 2. Se a variável NÃO for encontrada (ou seja, estamos rodando localmente),
//    use 'http://localhost:8080' como o valor padrão.
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

console.log(`API Base URL: ${baseURL}`); // Log de depuração útil!

const api = axios.create({
  baseURL: baseURL,
  // withCredentials: true, // Descomente se você precisar lidar com cookies de sessão entre domínios
});

export default api;
