import api from "./client";
import type { VeiculoCadastroRequest, VeiculoResponse } from "../types/veiculo";

export async function listarVeiculosPorCliente(idCliente: number): Promise<VeiculoResponse[]> {
  const response = await api.get<VeiculoResponse[]>(`/veiculos/cliente/${idCliente}`);
  return response.data;
}

export async function cadastrarVeiculo(request: VeiculoCadastroRequest): Promise<VeiculoResponse> {
  const response = await api.post<VeiculoResponse>("/veiculos", request);
  return response.data;
}