import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "../context/AuthContext";
import PrivateRoute from "../components/PrivateRoute";
import Layout from "../components/Layout";
import Home from "../pages/Home";
import Login from "../pages/Login";  
import Consultas from "../pages/Consultas";
import CadastroUsuario from "../pages/CadastroUsuario";
import GerenciarMedicos from "../pages/GerenciarMedicos";
import GerenciarPacientes from "../pages/GerenciarPacientes";
import EditarMedico from "../pages/EditarMedico";
import EditarPaciente from "../pages/EditarPaciente";




function Rotas() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Rotas Públicas */}
          <Route path="/login" element={<Login />} />
          <Route path="/cadastro" element={<CadastroUsuario />} />
          
          {/* Rotas Privadas */}
          <Route path="/home" element={
            <PrivateRoute>
              <Layout><Home /></Layout>
            </PrivateRoute>
          } />

          <Route path="/consultas" element={
            <PrivateRoute allowedRoles={["ROLE_ADMIN", "ROLE_MEDICO", "ROLE_PACIENTE"]}>
              <Layout><Consultas /></Layout>
            </PrivateRoute>
          } />

          {/* Rota para gerenciar médicos (Admin) */}
          <Route path="/medicos" element={
            <PrivateRoute allowedRoles={["ROLE_ADMIN"]}>
              <Layout>
                <GerenciarMedicos />
              </Layout>
            </PrivateRoute>
          } />

          <Route path="/medicos/editar/:id" element={
            <PrivateRoute allowedRoles={["ROLE_ADMIN", "ROLE_MEDICO"]}>
              <Layout>
                <EditarMedico />
              </Layout>
            </PrivateRoute>
          } />

          {/* Rota para gerenciar pacientes (Admin) */}
          <Route path="/pacientes" element={
            <PrivateRoute allowedRoles={["ROLE_ADMIN"]}>
              <Layout>
                <GerenciarPacientes />
              </Layout>
            </PrivateRoute>
          } />

          <Route path="/pacientes/editar/:id" element={
            <PrivateRoute allowedRoles={["ROLE_ADMIN", "ROLE_PACIENTE"]}>
              <Layout>
                <EditarPaciente />
              </Layout>
            </PrivateRoute>
          } />

        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default Rotas;