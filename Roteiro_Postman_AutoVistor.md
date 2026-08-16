# Roteiro Completo de Testes — Postman (AutoVistor)

Siga na ordem. Guarde os `id`s e `token`s retornados em cada passo — você vai precisar deles nos passos seguintes. Onde aparecer `{algumId}`, substitua pelo valor real que a resposta anterior te devolveu.

Base URL: `http://localhost:8080`

---

## 1. Cadastro de Cliente (público, sem token)

```
POST /clientes
Content-Type: application/json

{
  "nome": "Maria Silva",
  "cpf": "12345678901",
  "telefone": "61987654321",
  "email": "maria.silva@email.com",
  "senha": "senha12345"
}
```
→ Guarde o `id` retornado (**idCliente**).

## 2. Cadastro de Funcionário Vistoriador

Esse endpoint exige token de Gerente — se você já tem um Gerente cadastrado, pule para o passo 3 e volte aqui depois de logar como Gerente. Se **não** tiver nenhum Gerente ainda, você precisa criar um primeiro pelo banco de dados diretamente (via MySQL), já que o cadastro de funcionário é uma rota protegida.

```
POST /funcionarios
Authorization: Bearer {token do Gerente}
Content-Type: application/json

{
  "nome": "Carlos Andrade",
  "email": "carlos.andrade@empresa.com",
  "matricula": "VIS-001",
  "senha": "senha12345",
  "cargo": "VISTORIADOR"
}
```
→ Guarde o `id` (**idVistoriador**).

## 3. Login

Login é sempre por e-mail + senha, para qualquer perfil.

```
POST /auth/login
Content-Type: application/json

{
  "email": "maria.silva@email.com",
  "senha": "senha12345"
}
```
→ Guarde o `token` (**tokenCliente**). Repita esse mesmo passo trocando e-mail/senha para obter **tokenVistoriador** e **tokenGerente**.

---

## 4. Cadastro de Veículo (Cliente)

```
POST /veiculos
Authorization: Bearer {tokenCliente}
Content-Type: application/json

{
  "placa": "ABC1D23",
  "tipoVeiculo": "Carro",
  "nomeVeiculo": "Ford Fusion",
  "modelo": "Titanium",
  "anoVeiculo": 2020,
  "chassi": "ABC12345DEF678901",
  "observacoes": "Pequenos arranhões na porta",
  "idCliente": {idCliente}
}
```
→ Guarde o `id` (**idVeiculo**).

---

## 5. Agendamento (Cliente)

```
POST /agendamentos
Authorization: Bearer {tokenCliente}
Content-Type: application/json

{
  "dataAgendamento": "2026-09-15",
  "hora": "10:30:00",
  "tipoVistoria": "PREVIA",
  "idVeiculo": {idVeiculo}
}
```
→ Guarde o `id` (**idAgendamento**). Status inicial: `PENDENTE`.

---

## 6. Designar Vistoriador (Gerente)

```
POST /agendamentos/{idAgendamento}/designar-vistoriador
Authorization: Bearer {tokenGerente}
Content-Type: application/json

{
  "idFuncionario": {idVistoriador}
}
```
→ Status vira `CONFIRMADO`.

---

## 7. Registrar Vistoria com Checklist (Vistoriador)

```
POST /agendamentos/{idAgendamento}/vistoria
Authorization: Bearer {tokenVistoriador}
Content-Type: application/json

{
  "resultado": "APROVADO",
  "observacoes": "Veículo em bom estado geral.",
  "itens": [
    { "nomeItem": "Pneus", "situacao": "OK", "observacao": null },
    { "nomeItem": "Freios", "situacao": "OK", "observacao": null },
    { "nomeItem": "Motor", "situacao": "OK", "observacao": null },
    { "nomeItem": "Lataria", "situacao": "AVARIA", "observacao": "Arranhão na porta traseira esquerda" },
    { "nomeItem": "Vidros", "situacao": "OK", "observacao": null },
    { "nomeItem": "Luzes", "situacao": "OK", "observacao": null },
    { "nomeItem": "Suspensão", "situacao": "OK", "observacao": null },
    { "nomeItem": "Painel/Elétrica", "situacao": "OK", "observacao": null }
  ]
}
```
→ Guarde o `id` (**idVistoria**). Agendamento vira `CONCLUIDO`.

## 8. Upload de Foto (Vistoriador)

No Postman: aba **Body → form-data** (não JSON). Duas chaves:
- `arquivo` → tipo **File**, selecione uma imagem do seu computador
- `descricao` → tipo **Text**, ex: "Arranhão na porta traseira"

```
POST /agendamentos/{idAgendamento}/vistoria/{idVistoria}/fotos
Authorization: Bearer {tokenVistoriador}
```

## 9. Baixar Foto

```
GET /agendamentos/{idAgendamento}/vistoria/fotos/{idFoto}/download
Authorization: Bearer {qualquer token válido}
```

---

## 10. Pagamento à vista (Pix)

```
POST /vistorias/{idVistoria}/pagamento
Authorization: Bearer {tokenVistoriador}
Content-Type: application/json

{
  "formaPagamento": "PIX",
  "valor": 150.00
}
```
→ `statusPagamento: "PAGO"` imediatamente. Nota fiscal e lançamento de caixa gerados automaticamente.

## 10b. Pagamento via Boleto (fluxo alternativo — repita passos 5–9 com outro agendamento se quiser testar os dois)

```
POST /vistorias/{outraIdVistoria}/pagamento
Authorization: Bearer {tokenVistoriador}
Content-Type: application/json

{
  "formaPagamento": "BOLETO",
  "valor": 180.00
}
```
→ `statusPagamento: "PENDENTE"`. Guarde o `id` (**idPagamentoBoleto**).

Confirmar o boleto depois (Gerente):
```
POST /pagamentos/{idPagamentoBoleto}/confirmar-boleto
Authorization: Bearer {tokenGerente}
```

---

## 11. Laudo em PDF (Vistoriador)

```
POST /vistorias/{idVistoria}/laudo
Authorization: Bearer {tokenVistoriador}
```

Baixar:
```
GET /vistorias/{idVistoria}/laudo/download
Authorization: Bearer {qualquer token válido}
```

---

## 12. Relatórios (Gerente)

```
GET /relatorios/operacional?inicio=2026-01-01&fim=2026-12-31
Authorization: Bearer {tokenGerente}
```

```
GET /relatorios/financeiro?inicio=2026-01-01&fim=2026-12-31
Authorization: Bearer {tokenGerente}
```

---

## Ordem de dependência resumida

```
Cliente → Login → Veículo → Agendamento → Designar Vistoriador
  → Registrar Vistoria (+ checklist) → Upload Foto → Pagamento
  → (se boleto) Confirmar Boleto → Gerar Laudo → Relatórios
```

Se algum passo der erro, confira sempre: (1) o token é do perfil certo para aquele endpoint, (2) o status do agendamento/vistoria/pagamento está no estado esperado para a ação (ex: só dá pra registrar vistoria se o agendamento estiver `CONFIRMADO`).
