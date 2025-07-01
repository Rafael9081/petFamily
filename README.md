# PetFamily
# Canil API (PetFamily Canil Manager)

## Descrição

A **Canil API** é um sistema backend desenvolvido em Java com Spring Boot, projetado para gerenciar as operações de um canil. Ele permite o cadastro e acompanhamento de cães, suas ninhadas, despesas associadas, registros de vendas, características e histórico de vacinação.

## Entidades Principais do Domínio

O sistema modela as seguintes entidades principais:

*   **`Cachorro`**: Representa um cão no canil, incluindo seus dados básicos (nome, sexo, data de nascimento, raça), informações de parentesco (pai e mãe), tutor, histórico de despesas, ninhadas (se for fninhada de filhotes, vinculada a uma mãe e um pai.
*   **`Despesa`**: Modela os gastos associados a um cão específico (ex: alimentação, veterinário).
*   **`Tutor`**: Representa o proprietário ou responsável por um cão.
*   **`Venda`**: Contém as informações sobre a venda de um cão.
*   **`Caracteristica`**: Descreve características específicas de um cão.
*   **`Vacina`**: Registra as vacinas aplicadas a um cão.
*   **`Sexo`**: Enum para o sexo do cão (MACHO, FEMEA).
*   **`CategoriaDespesa`**: Enum para classificar os tipos de despesa.

## Funcionalidades Implementadas (e em Desenvolvimento)

*   **Gerenciamento de Cães:**
    *   CRUD completo para cães.
    *   Associação de pai e mãe.
    *   Registro de tutor.
    *   Marcação de cães como vendidos e associação com um registro de venda.
*   **Gerenciamento de Ninhadas:**
    *   Registro de novas ninhadas, com data de nascimento, quantidade de machos e fêmeas, e associação com mãe e pai.
    *   Listagem de ninhadas por mãe.
    *   Listagem de ninhadas por ano de nascimento.
*   **Gerenciamento de Despesas:**
    *   Registro de despesas para cães específicos, incluindo descrição, valor, data e categoria.
    *   Exclusão de despesas.
    *   Listagem de despesas por cão, período ou categoria.
    *   Cálculo do custo total de despesas por período.
*   **Relatórios Financeiros:**
    *   Geração de relatório financeiro para um cão individual, detalhando despesas, custo total e lucro da venda (se aplicável).
*   **Saúde e Características:**
    *   Adição de vacinas à carteira de vacinação de um cão.
    *   Adição de características a um cão.

## Tecnologias Utilizadas

*   **Java 17+** (ou a versão do seu JDK)
*   **Spring Boot** (para criação rápida de aplicações stand-alone baseadas em Spring)
    *   Spring Web (para criação de APIs RESTful)
    *   Spring Data JPA (para persistência de dados e interação com banco de dados)
*   **Maven** (para gerenciamento de depend e build do projeto)
*   **Banco de Dados Relacional** (ex: H2 para desenvolvimento, PostgreSQL, MySQL para produção - a ser especificado conforme configuração)
*   **JUnit 5** (para testes unitários e de integração)

## Estrutura do Projeto

O projeto segue uma arquitetura em camadas típica de aplicações Spring Boot:

*   `br.com.petfamily.canilapi.model`: Contém as entidades JPA que representam o domínio da aplicação.
*   `br.com.petfamily.canilapi.repository`: Interfaces do Spring Data JPA para interação com o banco de dados.
*   `br.com.petfamily.canilapi.service`: Classes de serviço que contêm a lógica de negócios da aplicação.
*   `br.com.petfamily.canilapi.controller`: (A ser criado) Controladores REST para expor os endpoints da API.
*   `br.com.petfamily.canilapi.exception`: (Sugestão) Classes de exceção customizadas.
