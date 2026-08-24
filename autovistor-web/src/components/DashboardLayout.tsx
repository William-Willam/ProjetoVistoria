import { Outlet, NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function DashboardLayout() {
  const { usuario, logout } = useAuth();

  const linkClasse = ({ isActive }: { isActive: boolean }) =>
    `block px-4 py-2 rounded-md text-sm ${
      isActive ? "bg-blue-500 text-white font-bold" : "text-slate-200 hover:bg-slate-600"
    }`;

  return (
    <div className="flex h-screen">
      <aside className="w-60 bg-slate-700 flex flex-col p-4">
        <h1 className="text-white text-xl font-bold">AutoVistor</h1>
        <p className="text-slate-300 text-xs mb-6">Cliente</p>

        <nav className="flex flex-col gap-1">
          <NavLink to="/dashboard/veiculos" className={linkClasse}>Meus Veículos</NavLink>
          <NavLink to="/dashboard/agendamentos" className={linkClasse}>Agendamentos</NavLink>
        </nav>

        <button
          onClick={logout}
          className="mt-auto bg-red-500 hover:bg-red-600 text-white rounded-md py-2 text-sm font-bold"
        >
          Sair
        </button>
      </aside>

      <main className="flex-1 bg-slate-50 p-6 overflow-auto">
        <Outlet />
      </main>
    </div>
  );
}