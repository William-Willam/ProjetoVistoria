import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { cadastrarCliente } from "../api/cliente";

export default function Cadastro() {
  const [nome, setNome] = useState("");
  const [cpf, setCpf] = useState("");
  const [telefone, setTelefone] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState(false);
  const [carregando, setCarregando] = useState(false);

  const navigate = useNavigate();

  async function handleCadastro(e: React.FormEvent) {
    e.preventDefault();
    setErro("");

    if (!nome.trim() || !cpf.trim() || !telefone.trim() || !email.trim() || !senha.trim()) {
      setErro("Preencha todos os campos.");
      return;
    }

    setCarregando(true);
    try {
      await cadastrarCliente({ nome, cpf, telefone, email, senha });
      setSucesso(true);
      setTimeout(() => navigate("/login"), 2000);
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

      <div className="flex-1 flex items-center justify-center bg-white overflow-auto py-8">
        <form onSubmit={handleCadastro} className="w-full max-w-sm px-6">
          <h2 className="text-2xl font-bold text-slate-800 mb-1">Criar conta</h2>
          <p className="text-slate-500 text-sm mb-6">Cadastre-se para agendar sua vistoria</p>

          {sucesso ? (
            <p className="text-green-600 text-sm mb-4">
              Cadastro realizado com sucesso! Redirecionando para o login...
            </p>
          ) : (
            <>
              <label className="block text-sm text-slate-700 mb-1">Nome completo</label>
              <input
                value={nome}
                onChange={(e) => setNome(e.target.value)}
                className="w-full border border-slate-300 rounded-md px-3 py-2 mb-3 focus:outline-none focus:border-blue-500"
              />

              <label className="block text-sm text-slate-700 mb-1">CPF</label>
              <input
                value={cpf}
                onChange={(e) => setCpf(e.target.value)}
                placeholder="Somente números"
                className="w-full border border-slate-300 rounded-md px-3 py-2 mb-3 focus:outline-none focus:border-blue-500"
              />

              <label className="block text-sm text-slate-700 mb-1">Telefone</label>
              <input
                value={telefone}
                onChange={(e) => setTelefone(e.target.value)}
                className="w-full border border-slate-300 rounded-md px-3 py-2 mb-3 focus:outline-none focus:border-blue-500"
              />

              <label className="block text-sm text-slate-700 mb-1">E-mail</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full border border-slate-300 rounded-md px-3 py-2 mb-3 focus:outline-none focus:border-blue-500"
              />

              <label className="block text-sm text-slate-700 mb-1">Senha</label>
              <input
                type="password"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                className="w-full border border-slate-300 rounded-md px-3 py-2 mb-4 focus:outline-none focus:border-blue-500"
              />

              {erro && <p className="text-red-600 text-sm mb-4">{erro}</p>}

              <button
                type="submit"
                disabled={carregando}
                className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-md py-2 disabled:opacity-50"
              >
                {carregando ? "Cadastrando..." : "Criar conta"}
              </button>
            </>
          )}

          <p className="text-center text-sm text-slate-500 mt-4">
            Já tem conta?{" "}
            <Link to="/login" className="text-blue-500 font-bold">
              Entrar
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
}