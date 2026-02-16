import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import PrivateRoute from "./components/PrivateRoute";
import Navbar from "./components/Navbar";
import Login from "./pages/Login";  
import Consultas from "./pages/Consultas";
import CadastroUsuario from "./pages/CadastroUsuario";
import GerenciarMedicos from "./pages/GerenciarMedicos";
import GerenciarPacientes from "./pages/GerenciarPacientes";
import EditarMedico from "./pages/EditarMedico";
import EditarPaciente from "./pages/EditarPaciente";

// Home estilizada com Bootstrap
const Home = () => (
  <div className="container mt-5">
    <div className="p-5 mb-4 bg-light rounded-3 shadow-sm text-center">
      <div className="container-fluid py-5">
        <h1 className="display-5 fw-bold text-primary">Bem-vindo à Clínica Salvador</h1>
        <p className="col-md-8 fs-4 mx-auto text-muted">
          Sistema de gestão de consultas e prontuários médicos.
        </p>
        <hr className="my-4" />
        <p>Acesse o menu "Minhas Consultas" para gerenciar seus agendamentos.</p>
      </div>
    </div>
  </div>
);

// Layout com Navbar e container para o conteúdo
const Layout = ({ children }) => (
  <div className="min-vh-100 bg-light">
    <Navbar />
    <main>
      {children}
    </main>
  </div>
);

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Rotas Públicas */}
          <Route path="/login" element={<Login />} />
          <Route path="/cadastro" element={<CadastroUsuario />} />
          
          {/* Rotas Privadas */}
          <Route path="/" element={
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

export default App;