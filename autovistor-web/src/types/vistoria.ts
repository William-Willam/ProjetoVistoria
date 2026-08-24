export interface ItemVistoriaResponse {
  id: number;
  nomeItem: string;
  situacao: "OK" | "AVARIA";
  observacao: string | null;
}

export interface VistoriaResponse {
  id: number;
  dataVistoria: string;
  resultado: "APROVADO" | "REPROVADO" | "APROVADO_COM_RESSALVAS";
  observacoes: string;
  idAgendamento: number;
  idFuncionario: number;
  itens: ItemVistoriaResponse[];
}

export interface PagamentoCadastroRequest {
  formaPagamento: string;
  valor: number;
}

export interface PagamentoResponse {
  id: number;
  formaPagamento: string;
  statusPagamento: string;
  valor: number;
  dataPagamento: string | null;
  idVistoria: number;
}