
import { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';

// Home estilizada com Bootstrap e agora com hooks
export default function Home() {
  const { user } = useContext(AuthContext);

  return (
    <div className="container mt-0 ">
      <div className="p-5 mb-4 bg-light rounded-3 shadow-sm text-center">
        <div className="container-fluid py-5 ">
          <img src="/clinica.svg" alt="Clínica Salvador" className="mb-0" style={{ width: '250px' }} />
          <h1 className="display-5 fw-bold text-primary">
            Bem-vindo(a), {user?.sub || 'usuário'}!
          </h1>
          <p className="col-md-8 fs-4 mx-auto text-muted">
            Sistema de gestão de consultas e prontuários médicos.
          </p>
          <hr className="my-4" />
          <p>Use o menu de navegação para acessar as funcionalidades do sistema.</p>
        </div>
      </div>
    </div>
  );
}