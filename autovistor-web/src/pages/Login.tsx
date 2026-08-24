import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { autenticar } from "../api/auth";
import { useAuth } from "../context/AuthContext";

export default function Login() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleLogin(e: React.FormEvent) {
    e.preventDefault();
    setErro("");

    if (!email.trim() || !senha.trim()) {
      setErro("Preencha e-mail e senha.");
      return;
    }

    setCarregando(true);
    try {
      const { token, usuario } = await autenticar({ email, senha });
      login(token, usuario);
      navigate("/dashboard");
    } catch (err: any) {
      const mensagem = err.response?.data?.mensagem ?? "Erro ao conectar com o servidor.";
      setErro(mensagem);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className="flex h-screen">
      <div className="hidden md:flex w-1/3 bg-slate-700 items-center justify-center">
        <div className="text-center px-8">
          <h1 className="text-white text-4xl font-bold">AutoVistor</h1>
          <p className="text-slate-300 mt-2">Gestão inteligente de vistorias veiculares</p>
        </div>
      </div>

      <div className="flex-1 flex items-center justify-center bg-white">
        <form onSubmit={handleLogin} className="w-full max-w-sm px-6">
          <h2 className="text-2xl font-bold text-slate-800 mb-1">Bem-vindo de volta</h2>
          <p className="text-slate-500 text-sm mb-6">Entre com sua conta para continuar</p>

          <label className="block text-sm text-slate-700 mb-1">E-mail</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="seu@email.com"
            className="w-full border border-slate-300 rounded-md px-3 py-2 mb-4 focus:outline-none focus:border-blue-500"
          />

          <label className="block text-sm text-slate-700 mb-1">Senha</label>
          <input
            type="password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            placeholder="Senha"
            className="w-full border border-slate-300 rounded-md px-3 py-2 mb-4 focus:outline-none focus:border-blue-500"
          />

          {erro && <p className="text-red-600 text-sm mb-4">{erro}</p>}

          <button
            type="submit"
            disabled={carregando}
            className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-md py-2 disabled:opacity-50"
          >
            {carregando ? "Entrando..." : "Entrar"}
          </button>

          <p className="text-center text-sm text-slate-500 mt-4">
            Não tem conta?{" "}
            <Link to="/cadastro" className="text-blue-500 font-bold">
              Criar conta
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
}