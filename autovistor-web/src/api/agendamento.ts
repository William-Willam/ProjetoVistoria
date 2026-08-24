import api from "./client";
import type { AgendamentoCadastroRequest, AgendamentoResponse, ReagendamentoRequest } from "../types/agendamento";

export async function listarAgendamentosPorCliente(idCliente: number): Promise<AgendamentoResponse[]> {
  const response = await api.get<AgendamentoResponse[]>(`/agendamentos/cliente/${idCliente}`);
  return response.data;
}

export async function criarAgendamento(request: AgendamentoCadastroRequest): Promise<AgendamentoResponse> {
  const response = await api.post<AgendamentoResponse>("/agendamentos", request);
  return response.data;
}

export async function reagendar(id: number, request: ReagendamentoRequest): Promise<AgendamentoResponse> {
  const response = await api.post<AgendamentoResponse>(`/agendamentos/${id}/reagendar`, request);
  return response.data;
}

export async function cancelar(id: number): Promise<void> {
  await api.post(`/agendamentos/${id}/cancelar`);
}