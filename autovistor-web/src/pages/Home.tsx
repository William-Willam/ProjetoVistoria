import { Link } from "react-router-dom";

const tipos = [
  {
    nome: "Vistoria Prévia",
    descricao:
      "Realizada antes da venda ou compra de um veículo, avalia o estado geral e identifica problemas ocultos antes de fechar negócio.",
  },
  {
    nome: "Vistoria Cautelar",
    descricao:
      "Verifica a procedência do veículo, cruzando dados de chassi, motor e histórico para identificar sinais de furto, roubo ou adulteração.",
  },
  {
    nome: "Vistoria de Transferência",
    descricao:
      "Exigida por órgãos de trânsito ao transferir a propriedade de um veículo entre pessoas, confirmando que os dados batem com o cadastrado.",
  },
];

export default function Home() {
  return (
    <div className="min-h-screen bg-white">
      <header className="flex items-center justify-between px-8 py-5 border-b border-slate-200">
        <h1 className="text-xl font-bold text-slate-800">AutoVistor</h1>
        <div className="flex gap-3 items-center">
          <Link to="/login" className="text-slate-700 font-bold px-4 py-2 text-sm">
            Entrar
          </Link>
          <Link
            to="/cadastro"
            className="bg-blue-500 hover:bg-blue-600 text-white font-bold px-5 py-2 rounded-md text-sm"
          >
            Criar conta
          </Link>
        </div>
      </header>

      <section className="text-center px-6 py-20 bg-slate-700">
        <h2 className="text-white text-4xl font-bold mb-4">
          Vistoria veicular sem burocracia
        </h2>
        <p className="text-slate-300 max-w-xl mx-auto">
          Agende, acompanhe e receba o laudo da vistoria do seu veículo em um só lugar,
          com checklist detalhado e fotos de cada etapa.
        </p>
        <Link
          to="/cadastro"
          className="inline-block mt-8 bg-blue-500 hover:bg-blue-600 text-white font-bold px-6 py-3 rounded-md"
        >
          Começar agora
        </Link>
      </section>

      <section className="px-8 py-16 max-w-4xl mx-auto">
        <h3 className="text-2xl font-bold text-slate-800 mb-2 text-center">
          O que é uma vistoria veicular?
        </h3>
        <p className="text-slate-600 text-center max-w-2xl mx-auto mb-12">
          É uma avaliação técnica do estado e da procedência de um veículo, realizada por um
          vistoriador credenciado. O resultado é registrado em um laudo, com checklist de itens
          verificados e fotos como evidência.
        </p>

        <div className="grid md:grid-cols-3 gap-6">
          {tipos.map((tipo) => (
            <div key={tipo.nome} className="border border-slate-200 rounded-lg p-6">
              <h4 className="font-bold text-slate-800 mb-2">{tipo.nome}</h4>
              <p className="text-slate-600 text-sm">{tipo.descricao}</p>
            </div>
          ))}
        </div>
      </section>

      <footer className="text-center text-slate-400 text-sm py-8 border-t border-slate-200">
        AutoVistor — Sistema de Vistoria Veicular
      </footer>
    </div>
  );
}