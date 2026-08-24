import api from "./client";
import type { VistoriaResponse, PagamentoCadastroRequest, PagamentoResponse } from "../types/vistoria";

export async function buscarVistoriaPorAgendamento(idAgendamento: number): Promise<VistoriaResponse> {
  const response = await api.get<VistoriaResponse>(`/agendamentos/${idAgendamento}/vistoria`);
  return response.data;
}

export async function registrarPagamento(
  idVistoria: number,
  request: PagamentoCadastroRequest
): Promise<PagamentoResponse> {
  const response = await api.post<PagamentoResponse>(`/vistorias/${idVistoria}/pagamento`, request);
  return response.data;
}

export async function baixarLaudo(idVistoria: number, nomeArquivo: string): Promise<void> {
  const response = await api.get(`/vistorias/${idVistoria}/laudo/download`, {
    responseType: "blob",
  });

  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement("a");
  link.href = url;
  link.download = nomeArquivo;
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
}