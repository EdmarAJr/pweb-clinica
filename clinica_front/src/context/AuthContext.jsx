// src/context/AuthContext.jsx
import { createContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import api from "../services/api";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Configura um interceptor para injetar o token em TODAS as requisições automaticamente
    // Isso é mais seguro que configurar api.defaults.headers
    const interceptorId = api.interceptors.request.use((config) => {
      const token = localStorage.getItem("token");
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        //console.log("Interceptor: Token adicionado ao header Authorization.");
      }
      return config;
    }, (error) => Promise.reject(error));

    const token = localStorage.getItem("token");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        //console.log("Payload do Token (Refresh):", decoded);

        // Tenta pegar roles de 'roles' ou 'authorities' e trata se for array de objetos
        const rolesClaim = decoded.roles || decoded.authorities || [];
        const roles = Array.isArray(rolesClaim) ? rolesClaim.map(r => r.authority || r) : [];

        setUser({ 
            id: decoded.pacienteId || decoded.medicoId, // Tenta pegar id de paciente ou médico
            sub: decoded.sub, 
            roles: roles
        });
      } catch (error) {
        logout();
      }
    }
    setLoading(false);

    return () => {
      api.interceptors.request.eject(interceptorId);
    };
  }, []);

  
  const login = async (username, password) => {

    //console.log("Tentando logar com:", username, password);
    
    // FIX: Limpa token antigo e remove o header Authorization.
    // Isso evita que o axios envie um token expirado/inválido, o que causaria erro 403 imediato.
    localStorage.removeItem("token");

    try {
      // Envia o objeto diretamente. Template literals `${}` não são necessários aqui.
      const response = await api.post("/auth/login", { username, password });
      const { token } = response.data; 

      
      //console.log("Token recebido:", token); // Supondo que o backend retorna { token: "..." }
      
      localStorage.setItem("token", token);
      const decoded = jwtDecode(token);
      //console.log("Payload do Token (Login):", decoded);

      const rolesClaim = decoded.roles || decoded.authorities || [];
      const roles = Array.isArray(rolesClaim) ? rolesClaim.map(r => r.authority || r) : [];
      
      setUser({ 
          id: decoded.pacienteId || decoded.medicoId,
          sub: decoded.sub, 
          roles: roles 
      });
      
      return true;
    } catch (error) {
      alert("Falha no login. Verifique suas credenciais: ", JSON.stringify(error.response?.data || error.message));
      //console.error("Erro ao logar", error);
      // Log detalhado para identificar a causa do 403 (ex: mensagem do backend)
      if (error.response) {
        alert(`Erro ${error.response.status}: ${error.response.data.message || "Sem mensagem detalhada"}`);
        //console.error("Status:", error.response.status);
        //console.error("Dados:", error.response.data);
      }
      return false;
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
  };

  const hasRole = (role) => {
    return user?.roles.includes(role);
  };

  return (
    <AuthContext.Provider value={{ user, signed: !!user, login, logout, loading, hasRole }}>
      {children}
    </AuthContext.Provider>
  );
};