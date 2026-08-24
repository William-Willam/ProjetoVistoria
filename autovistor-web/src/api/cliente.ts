import api from "./client";
import type { ClienteCadastroRequest } from "../types/cliente";

export async function cadastrarCliente(request: ClienteCadastroRequest): Promise<void> {
  await api.post("/clientes", request);
}