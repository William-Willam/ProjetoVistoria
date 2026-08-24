import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Cadastro from "./pages/Cadastro";
import RotaProtegida from "./components/RotaProtegida";
import DashboardLayout from "./components/DashboardLayout";
import Veiculos from "./pages/Veiculos";
import Agendamentos from "./pages/Agendamentos";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/cadastro" element={<Cadastro />} />

        <Route element={<RotaProtegida />}>
          <Route path="/dashboard" element={<DashboardLayout />}>
            <Route index element={<Navigate to="veiculos" replace />} />
            <Route path="veiculos" element={<Veiculos />} />
            <Route path="agendamentos" element={<Agendamentos />} />
          </Route>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;