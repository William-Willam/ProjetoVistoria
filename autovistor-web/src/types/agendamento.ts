export interface AgendamentoResponse {
  id: number;
  dataAgendamento: string;
  hora: string;
  tipoVistoria: "TRANSFERENCIA" | "CAUTELAR" | "PREVIA";
  statusAgendamento: "PENDENTE" | "CONFIRMADO" | "CONCLUIDO" | "CANCELADO" | "REAGENDADO";
  idCliente: number;
  idVeiculo: number;
  idFuncionario: number | null;
}

export interface AgendamentoCadastroRequest {
  dataAgendamento: string;
  hora: string;
  tipoVistoria: string;
  idVeiculo: number;
}

export interface ReagendamentoRequest {
  novaData: string;
  novaHora: string;
}