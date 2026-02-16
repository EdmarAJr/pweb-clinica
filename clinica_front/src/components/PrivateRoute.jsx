// src/components/PrivateRoute.jsx
import { useContext } from "react";
import { Navigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";

const PrivateRoute = ({ children, allowedRoles }) => {
  const { signed, loading, user } = useContext(AuthContext);

  if (loading) return <div>Carregando...</div>;

  if (!signed) {
    return <Navigate to="/login" />;
  }

  // Se houver roles específicas exigidas, verifica se o usuário tem alguma delas
  if (allowedRoles) {
    const hasPermission = allowedRoles.some(role => user.roles.includes(role));
    //console.log(`PrivateRoute: Requer [${allowedRoles}] | Usuário tem [${user.roles}]`);
    if (!hasPermission) {
      return <div style={{padding: 20}}>Acesso negado. Você não tem permissão para ver esta página.</div>;
    }
  }

  return children;
};

export default PrivateRoute;