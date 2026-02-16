import { Link, useNavigate } from "react-router-dom";
import { useContext } from "react";
import { AuthContext } from "../context/AuthContext";

const Navbar = () => {
  const { logout, user, hasRole } = useContext(AuthContext);
  const navigate = useNavigate();
    
  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm mb-4">
      <div className="container">
        <Link className="navbar-brand fw-bold" to="/">
          <i className="bi bi-heart-pulse-fill me-2"></i>
          Clínica Salvador
        </Link>
        
        <button 
          className="navbar-toggler" 
          type="button" 
          data-bs-toggle="collapse" 
          data-bs-target="#navbarNav" 
          aria-controls="navbarNav" 
          aria-expanded="false" 
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <Link className="nav-link active" to="/consultas">Minhas consultas</Link>
            </li>
            {!hasRole("ROLE_ADMIN") && (hasRole("ROLE_MEDICO") || hasRole("ROLE_PACIENTE")) && (
              <li className="nav-item">
                <Link className="nav-link" to={hasRole("ROLE_MEDICO") ? `/medicos/editar/${user?.id}` : `/pacientes/editar/${user?.id}`}>Editar perfil</Link>
              </li>
            )}
            
            {hasRole("ROLE_ADMIN") && (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/medicos">Gerenciar médicos</Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/pacientes">Gerenciar pacientes</Link>
                </li>
              </>
            )}
          </ul>

          <div className="d-flex align-items-center">
            <span className="navbar-text me-3 text-white fw-semibold">
              Olá, <span className="text-warning">{user?.sub || "Usuário"}</span>
            </span>
            <button 
              onClick={handleLogout} 
              className="btn btn-outline-light btn-sm fw-bold px-3"
            >
              Sair
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;