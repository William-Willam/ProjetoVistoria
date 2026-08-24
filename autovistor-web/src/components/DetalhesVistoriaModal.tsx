import { useEffect, useState } from "react";
import Modal from "./Modal";
import { buscarVistoriaPorAgendamento, registrarPagamento, baixarLaudo } from "../api/vistoria";
import type { VistoriaResponse } from "../types/vistoria";

interface Props {
  idAgendamento: number | null;
  onFechar: () => void;
}

const rotulosResultado: Record<string, string> = {
  APROVADO: "Aprovado",
  REPROVADO: "Reprovado",
  APROVADO_COM_RESSALVAS: "Aprovado com ressalvas",
};

export default function DetalhesVistoriaModal({ idAgendamento, onFechar }: Props) {
  const [vistoria, setVistoria] = useState<VistoriaResponse | null>(null);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");

  const [formaPagamento, setFormaPagamento] = useState("PIX");
  const [valor, setValor] = useState("");
  const [pagando, setPagando] = useState(false);
  const [erroPagamento, setErroPagamento] = useState("");
  const [pagamentoRealizado, setPagamentoRealizado] = useState(false);

  useEffect(() => {
    if (idAgendamento !== null) {
      carregar(idAgendamento);
    }
  }, [idAgendamento]);

  async function carregar(id: number) {
    setCarregando(true);
    setErro("");
    setPagamentoRealizado(false);
    try {
      const dados = await buscarVistoriaPorAgendamento(id);
      setVistoria(dados);
    } catch {
      setErro("Erro ao carregar detalhes da vistoria.");
    } finally {
      setCarregando(false);
    }
  }

  async function handlePagar(e: React.FormEvent) {
    e.preventDefault();
    setErroPagamento("");

    if (!valor.trim()) {
      setErroPagamento("Informe o valor.");
      return;
    }
    if (!vistoria) return;

    setPagando(true);
    try {
      await registrarPagamento(vistoria.id, { formaPagamento, valor: Number(valor) });
      setPagamentoRealizado(true);
    } catch (err: any) {
      setErroPagamento(err.response?.data?.mensagem ?? "Erro ao registrar pagamento.");
    } finally {
      setPagando(false);
    }
  }

  async function handleBaixarLaudo() {
    if (!vistoria) return;
    try {
      await baixarLaudo(vistoria.id, `laudo-vistoria-${vistoria.id}.pdf`);
    } catch {
      alert("Erro ao baixar laudo.");
    }
  }

  return (
    <Modal titulo="Detalhes da Vistoria" aberto={idAgendamento !== null} onFechar={onFechar}>
      {carregando && <p className="text-slate-500">Carregando...</p>}
      {erro && <p className="text-red-600">{erro}</p>}

      {vistoria && (
        <div className="space-y-4 max-h-[70vh] overflow-y-auto">
          <div>
            <p className="text-sm text-slate-500">Resultado</p>
            <p className="font-bold text-slate-800">{rotulosResultado[vistoria.resultado]}</p>
          </div>

          <div>
            <p className="text-sm text-slate-500">Observações</p>
            <p className="text-slate-700 text-sm">{vistoria.observacoes}</p>
          </div>

          <div>
            <p className="text-sm text-slate-500 mb-1">Checklist</p>
            <ul className="text-sm space-y-1">
              {vistoria.itens.map((item) => (
                <li key={item.id} className="flex justify-between border-b border-slate-100 py-1">
                  <span>{item.nomeItem}</span>
                  <span className={item.situacao === "OK" ? "text-green-600" : "text-red-600"}>
                    {item.situacao === "OK" ? "OK" : "Avaria"}
                  </span>
                </li>
              ))}
            </ul>
          </div>

          <button
            onClick={handleBaixarLaudo}
            className="w-full bg-slate-700 hover:bg-slate-800 text-white font-bold rounded-md py-2 text-sm"
          >
            Baixar Laudo (PDF)
          </button>

          <div className="border-t border-slate-200 pt-4">
            <p className="text-sm font-bold text-slate-700 mb-2">Pagamento</p>

            {pagamentoRealizado ? (
              <p className="text-green-600 text-sm">Pagamento registrado com sucesso!</p>
            ) : (
              <form onSubmit={handlePagar} className="space-y-3">
                <div>
                  <label className="block text-sm text-slate-700 mb-1">Forma de pagamento</label>
                  <select
                    value={formaPagamento}
                    onChange={(e) => setFormaPagamento(e.target.value)}
                    className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
                  >
                    <option value="PIX">Pix</option>
                    <option value="DEBITO">Débito</option>
                    <option value="CREDITO">Crédito</option>
                    <option value="BOLETO">Boleto</option>
                    <option value="DINHEIRO">Dinheiro</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm text-slate-700 mb-1">Valor (R$)</label>
                  <input
                    type="number"
                    step="0.01"
                    value={valor}
                    onChange={(e) => setValor(e.target.value)}
                    className="w-full border border-slate-300 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
                  />
                </div>

                {erroPagamento && <p className="text-red-600 text-sm">{erroPagamento}</p>}

                <button
                  type="submit"
                  disabled={pagando}
                  className="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-md py-2 disabled:opacity-50"
                >
                  {pagando ? "Processando..." : "Pagar"}
                </button>
              </form>
            )}
          </div>
        </div>
      )}
    </Modal>
  );
}