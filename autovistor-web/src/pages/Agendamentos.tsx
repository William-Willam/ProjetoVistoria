import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import {
  listarAgendamentosPorCliente,
  criarAgendamento,
  reagendar,
  cancelar,
} from "../api/agendamento";
import { listarVeiculosPorCliente } from "../api/veiculo";
import type { AgendamentoResponse } from "../types/agendamento";
import type { VeiculoResponse } from "../types/veiculo";
import Modal from "../components/Modal";
import DetalhesVistoriaModal from "../components/DetalhesVistoriaModal";

const rotulosStatus: Record<string, string> = {
  PENDENTE: "Pendente",
  CONFIRMADO: "Confirmado",
  CONCLUIDO: "Concluído",
  CANCELADO: "Cancelado",
  REAGENDADO: "Reagendado",
};

const coresStatus: Record<string, string> = {
  PENDENTE: "bg-yellow-100 text-yellow-700",
  CONFIRMADO: "bg-blue-100 text-blue-700",
  CONCLUIDO: "bg-green-100 text-green-700",
  CANCELADO: "bg-red-100 text-red-700",
  REAGENDADO: "bg-purple-100 text-purple-700",
};

const statusPermiteAcao = ["PENDENTE", "CONFIRMADO"];

export default function Agendamentos() {
  const { usuario } = useAuth();
  const [agendamentos, setAgendamentos] = useState<AgendamentoResponse[]>([]);
  const [veiculos, setVeiculos] = useState<VeiculoResponse[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  const [modalNovoAberto, setModalNovoAberto] = useState(false);
  const [modalReagendarAberto, setModalReagendarAberto] = useState<AgendamentoResponse | null>(null);
  const [agendamentoDetalhes, setAgendamentoDetalhes] = useState<number | null>(null);

  const [idVeiculo, setIdVeiculo] = useState("");
  const [tipoVistoria, setTipoVistoria] = useState("PREVIA");
  const [data, setData] = useState("");
  const [hora, setHora] = useState("");
  const [erroForm, setErroForm] = useState("");
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    carregarDados();
  }, []);

  async function carregarDados() {
    if (!usuario) return;
    setCarregando(true);
    setErro("");
    try {
      const [agendamentosDados, veiculosDados] = await Promise.all([
        listarAgendamentosPorCliente(usuario.id),
        listarVeiculosPorCliente(usuario.id),
      ]);
      setAgendamentos(agendamentosDados);
      setVeiculos(veiculosDados);
    } catch {
      setErro("Erro ao carregar agendamentos.");
    } finally {
      setCarregando(false);
    }
  }

  function limparFormularioNovo() {
    setIdVeiculo("");
    setTipoVistoria("PREVIA");
    setData("");
    setHora("");
    setErroForm("");
  }

  async function handleCriar(e: React.FormEvent) {
    e.preventDefault();
    setErroForm("");

    if (!idVeiculo || !data || !hora) {
      setErroForm("Preencha todos os campos.");
      return;
    }

    setSalvando(true);
    try {
      await criarAgendamento({
        dataAgendamento: data,
        hora: hora + ":00",
        tipoVistoria,
        idVeiculo: Number(idVeiculo),
      });
      setModalNovoAberto(false);
      limparFormularioNovo();
      carregarDados();
    } catch (err: any) {
      setErroForm(err.response?.data?.mensagem ?? "Erro ao agendar vistoria.");
    } finally {
      setSalvando(false);
    }
  }

  async function handleReagendar(e: React.FormEvent) {
    e.preventDefault();
    setErroForm("");
    if (!modalReagendarAberto || !data || !hora) {
      setErroForm("Preencha data e horário.");
      return;
    }

    setSalvando(true);
    try {
      await reagendar(modalReagendarAberto.id, { novaData: data, novaHora: hora + ":00" });
      setModalReagendarAberto(null);
      setData("");
      setHora("");
      carregarDados();
    } catch (err: any) {
      setErroForm(err.response?.data?.mensagem ?? "Erro ao reagendar.");
    } finally {
      setSalvando(false);
    }
  }

  async function handleCancelar(id: number) {
    if (!confirm("Deseja realmente cancelar este agendamento?")) return;
    try {
      await cancelar(id);
      carregarDados();
    } catch (err: any) {
      alert(err.response?.data?.mensagem ?? "Erro ao cancelar.");
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-slate-800">Agendamentos</h2>
        <button
          onClick={() => setModalNovoAberto(true)}
          disabled={veiculos.length === 0}
          className="bg-blue-500 hover:bg-blue-600 text-white font-bold px-4 py-2 rounded-md text-sm disabled:opacity-50"
        >
          + Novo Agendamento
        </button>
      </div>

      {veiculos.length === 0 && (
        <p className="text-yellow-700 bg-yellow-50 border border-yellow-200 rounded-md px-4 py-2 mb-4 text-sm">
          Cadastre um veículo antes de agendar uma vistoria.
        </p>
      )}
      {erro && <p className="text-red-600 mb-4">{erro}</p>}

      {carregando ? (
        <p className="text-slate-500">Carregando...</p>
      ) : agendamentos.length === 0 ? (
        <p className="text-slate-500">Nenhum agendamento ainda.</p>
      ) : (
        <div className="bg-white rounded-lg border border-slate-200 overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-slate-700 text-white">
              <tr>
                <th className="text-left px-4 py-2">Data</th>
                <th className="text-left px-4 py-2">Hora</th>
                <th className="text-left px-4 py-2">Tipo</th>
                <th className="text-left px-4 py-2">Status</th>
                <th className="text-left px-4 py-2">Ações</th>
              </tr>
            </thead>
            <tbody>
              {agendamentos.map((a, i) => (
                <tr key={a.id} className={i % 2 === 0 ? "bg-white" : "bg-slate-50"}>
                  <td className="px-4 py-2">{a.dataAgendamento}</td>
                  <td className="px-4 py-2">{a.hora}</td>
                  <td className="px-4 py-2">{a.tipoVistoria}</td>
                  <td className="px-4 py-2">
                    <span className={`px-2 py-1 rounded-full text-xs font-bold ${coresStatus[a.statusAgendamento]}`}>
                      {rotulosStatus[a.statusAgendamento]}
                    </span>
                  </td>
                  <td className="px-4 py-2">
                    {statusPermiteAcao.includes(a.statusAgendamento) ? (
                      <div className="flex gap-2">
                        <button
                          onClick={() => {
                            setModalReagendarAberto(a);
                            setData("");
                            setHora("");
                            setErroForm("");
                          }}
                          className="text-blue-600 hover:underline text-xs font-bold"
                        >
                          Reagendar
                        </button>
                        <button
                          onClick={() => handleCancelar(a.id)}
                          className="text-red-600 hover:underline text-xs font-bold"
                        >
                          Cancelar
                        </button>
                      </div>
                    ) : a.statusAgendamento === "CONCLUIDO" ? (
                      <button
                        onClick={() => setAgendamentoDetalhes(a.id)}
                        className="text-blue-600 hover:underline text-xs font-bold"
                      >
                        Ver Detalhes
                      </button>
                    ) : (
                      <span className="text-slate-400 text-xs">—</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Modal: Novo Agendamento */}
      <Modal titulo="Agendar Vistoria" aberto={modalNovoAberto} onFechar={() => setModalNovoAberto(false)}>
        <form onSubmit={handleCriar} className="space-y-3">
          <div>
            <label className="block text-sm text-slate-700 mb-1">Veículo</label>
            <select
              value={idVeiculo}
              onChange={(e) => setIdVeiculo(e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
            >
              <option value="">Selecione...</option>
              {veiculos.map((v) => (
                <option key={v.id} value={v.id}>
                  {v.nomeVeiculo} — {v.placa}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm text-slate-700 mb-1">Tipo de vistoria</label>
            <select
              value={tipoVistoria}
              onChange={(e) => setTipoVistoria(e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
            >
              <option value="PREVIA">Prévia</option>
              <option value="CAUTELAR">Cautelar</option>
              <option value="TRANSFERENCIA">Transferência</option>
            </select>
          </div>
          <div>
            <label className="block text-sm text-slate-700 mb-1">Data</label>
            <input
              type="date"
              value={data}
              onChange={(e) => setData(e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm text-slate-700 mb-1">Horário</label>
            <input
              type="time"
              value={hora}
              onChange={(e) => setHora(e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
            />
          </div>

          {erroForm && <p className="text-red-600 text-sm">{erroForm}</p>}

          <button
            type="submit"
            disabled={salvando}
            className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-md py-2 disabled:opacity-50"
          >
            {salvando ? "Agendando..." : "Agendar"}
          </button>
        </form>
      </Modal>

      {/* Modal: Reagendar */}
      <Modal
        titulo={`Reagendar — Agendamento #${modalReagendarAberto?.id ?? ""}`}
        aberto={modalReagendarAberto !== null}
        onFechar={() => setModalReagendarAberto(null)}
      >
        <form onSubmit={handleReagendar} className="space-y-3">
          <div>
            <label className="block text-sm text-slate-700 mb-1">Nova data</label>
            <input
              type="date"
              value={data}
              onChange={(e) => setData(e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm text-slate-700 mb-1">Novo horário</label>
            <input
              type="time"
              value={hora}
              onChange={(e) => setHora(e.target.value)}
              className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
            />
          </div>

          {erroForm && <p className="text-red-600 text-sm">{erroForm}</p>}

          <button
            type="submit"
            disabled={salvando}
            className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-md py-2 disabled:opacity-50"
          >
            {salvando ? "Salvando..." : "Confirmar novo horário"}
          </button>
        </form>
      </Modal>

      <DetalhesVistoriaModal
        idAgendamento={agendamentoDetalhes}
        onFechar={() => setAgendamentoDetalhes(null)}
      />
    </div>
  );
}