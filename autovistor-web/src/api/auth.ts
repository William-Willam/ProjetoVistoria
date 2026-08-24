import api from "./client";
import { jwtDecode } from "jwt-decode";
import type { LoginRequest, LoginResponse } from "../types/auth";
import type { Usuario } from "../types/usuario";

interface TokenPayload {
  id: number;
  tipo: string;
  role: string;
}

export async function autenticar(request: LoginRequest): Promise<{ token: string; usuario: Usuario }> {
  const response = await api.post<LoginResponse>("/auth/login", request);
  const { token } = response.data;

  const payload = jwtDecode<TokenPayload>(token);

  const usuario: Usuario = {
    id: payload.id,
    email: request.email,
    tipo: payload.tipo,
    role: payload.role,
  };

  return { token, usuario };
}