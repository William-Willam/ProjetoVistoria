import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { listarVeiculosPorCliente, cadastrarVeiculo } from "../api/veiculo";
import type { VeiculoResponse } from "../types/veiculo";
import Modal from "../components/Modal";

export default function Veiculos() {
  const { usuario } = useAuth();
  const [veiculos, setVeiculos] = useState<VeiculoResponse[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [modalAberto, setModalAberto] = useState(false);

  const [placa, setPlaca] = useState("");
  const [tipoVeiculo, setTipoVeiculo] = useState("");
  const [nomeVeiculo, setNomeVeiculo] = useState("");
  const [modelo, setModelo] = useState("");
  const [anoVeiculo, setAnoVeiculo] = useState("");
  const [chassi, setChassi] = useState("");
  const [observacoes, setObservacoes] = useState("");
  const [erroForm, setErroForm] = useState("");
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    carregarVeiculos();
  }, []);

  async function carregarVeiculos() {
    if (!usuario) return;
    setCarregando(true);
    setErro("");
    try {
      const dados = await listarVeiculosPorCliente(usuario.id);
      setVeiculos(dados);
    } catch {
      setErro("Erro ao carregar veículos.");
    } finally {
      setCarregando(false);
    }
  }

  function limparFormulario() {
    setPlaca("");
    setTipoVeiculo("");
    setNomeVeiculo("");
    setModelo("");
    setAnoVeiculo("");
    setChassi("");
    setObservacoes("");
    setErroForm("");
  }

  async function handleSalvar(e: React.FormEvent) {
    e.preventDefault();
    setErroForm("");

    if (!placa.trim() || !tipoVeiculo.trim() || !nomeVeiculo.trim() || !modelo.trim() || !anoVeiculo.trim() || !chassi.trim()) {
      setErroForm("Todos os campos, exceto observações, são obrigatórios.");
      return;
    }

    if (!usuario) return;

    setSalvando(true);
    try {
      await cadastrarVeiculo({
        placa,
        tipoVeiculo,
        nomeVeiculo,
        modelo,
        anoVeiculo: Number(anoVeiculo),
        chassi,
        observacoes,
        idCliente: usuario.id,
      });
      setModalAberto(false);
      limparFormulario();
      carregarVeiculos();
    } catch (err: any) {
      const mensagem = err.response?.data?.mensagem ?? "Erro ao cadastrar veículo.";
      setErroForm(mensagem);
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-slate-800">Meus Veículos</h2>
        <button
          onClick={() => setModalAberto(true)}
          className="bg-blue-500 hover:bg-blue-600 text-white font-bold px-4 py-2 rounded-md text-sm"
        >
          + Novo Veículo
        </button>
      </div>

      {erro && <p className="text-red-600 mb-4">{erro}</p>}

      {carregando ? (
        <p className="text-slate-500">Carregando...</p>
      ) : veiculos.length === 0 ? (
        <p className="text-slate-500">Nenhum veículo cadastrado ainda.</p>
      ) : (
        <div className="bg-white rounded-lg border border-slate-200 overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-slate-700 text-white">
              <tr>
                <th className="text-left px-4 py-2">Placa</th>
                <th className="text-left px-4 py-2">Veículo</th>
                <th className="text-left px-4 py-2">Modelo</th>
                <th className="text-left px-4 py-2">Ano</th>
              </tr>
            </thead>
            <tbody>
              {veiculos.map((v, i) => (
                <tr key={v.id} className={i % 2 === 0 ? "bg-white" : "bg-slate-50"}>
                  <td className="px-4 py-2">{v.placa}</td>
                  <td className="px-4 py-2">{v.nomeVeiculo}</td>
                  <td className="px-4 py-2">{v.modelo}</td>
                  <td className="px-4 py-2">{v.anoVeiculo}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <Modal titulo="Cadastrar Veículo" aberto={modalAberto} onFechar={() => setModalAberto(false)}>
        <form onSubmit={handleSalvar} className="space-y-3">
          <div>
            <label className="block text-sm text-slate-700 mb-1">Placa</label>
            <input
              value={placa}
              onChange={(e) => setPlaca(e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm text-slate-700 mb-1">Tipo</label>
            <input
              value={tipoVeiculo}
              onChange={(e) => setTipoVeiculo(e.target.value)}
              placeholder="Ex: Carro, Moto"
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm text-slate-700 mb-1">Nome do veículo</label>
            <input
              value={nomeVeiculo}
              onChange={(e) => setNomeVeiculo(e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm text-slate-700 mb-1">Modelo</label>
            <input
              value={modelo}
              onChange={(e) => setModelo(e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm text-slate-700 mb-1">Ano</label>
            <input
              type="number"
              value={anoVeiculo}
              onChange={(e) => setAnoVeiculo(e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm text-slate-700 mb-1">Chassi</label>
            <input
              value={chassi}
              onChange={(e) => setChassi(e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm text-slate-700 mb-1">Observações</label>
            <textarea
              value={observacoes}
              onChange={(e) => setObservacoes(e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
              rows={2}
            />
          </div>

          {erroForm && <p className="text-red-600 text-sm">{erroForm}</p>}

          <button
            type="submit"
            disabled={salvando}
            className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-md py-2 disabled:opacity-50"
          >
            {salvando ? "Salvando..." : "Cadastrar"}
          </button>
        </form>
      </Modal>
    </div>
  );
}