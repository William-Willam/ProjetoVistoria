# AutoVistor — Documentação do Projeto

Sistema de vistoria veicular em arquitetura de 3 camadas: **backend** (Spring Boot), **desktop** (JavaFX, para vistoriadores e gerentes) e **frontend web** (React, para clientes — não iniciado).

Este projeto aplica as correções e melhorias identificadas na análise de um sistema anterior do mesmo domínio: checagem de conflito de horário em agendamentos, edição/cancelamento/reagendamento, hash de senha, credenciais fora do código, histórico de desligamento preservado, e módulo financeiro completo (boleto, nota fiscal, caixa).

## Índice

1. [Documento de Requisitos](01-Documento-de-Requisitos.md) — requisitos funcionais e não funcionais (versão original)
2. [Modelagem de Banco de Dados](02-Modelagem-Banco-de-Dados.md) — schema SQL e diagrama de entidades (versão original)
3. [Arquitetura Técnica](03-Arquitetura-Tecnica.md) — camadas do backend, desktop, frontend, endpoints e fluxos (versão original)
4. [Manual do Usuário](04-Manual-do-Usuario.md) — guia de uso por perfil (Cliente, Vistoriador, Gerente) — *pendente de revisão pós-implementação*
5. [Registro de Alterações](05-Registro-de-Alteracoes.md) — **o que mudou desde os documentos originais**, e por quê (login por e-mail, correção do RF003, checklist/fotos na vistoria, correções de schema, endpoints reais, versões de stack, lacunas conhecidas)

> Os documentos 1–3 registram o planejamento inicial e permanecem como estavam. Para saber o que de fato foi implementado e onde isso diverge do planejamento, comece pelo documento 5.

## Nome do projeto
AutoVistor
