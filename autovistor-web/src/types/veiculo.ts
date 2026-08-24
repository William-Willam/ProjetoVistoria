export interface VeiculoResponse {
  id: number;
  placa: string;
  tipoVeiculo: string;
  nomeVeiculo: string;
  modelo: string;
  anoVeiculo: number;
  chassi: string;
  observacoes: string | null;
  idCliente: number;
}

export interface VeiculoCadastroRequest {
  placa: string;
  tipoVeiculo: string;
  nomeVeiculo: string;
  modelo: string;
  anoVeiculo: number;
  chassi: string;
  observacoes: string;
  idCliente: number;
}