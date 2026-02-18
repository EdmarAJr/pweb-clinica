import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

const GerenciarMedicos = () => {
  const [medicos, setMedicos] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    carregarMedicos();
  }, []);

  const carregarMedicos = async () => {
    try {
      const response = await api.get("/medicos/listar");
      setMedicos(Array.isArray(response.data) ? response.data : response.data.content || []);
    } catch (error) {
      alert("Erro ao carregar médicos: " + JSON.stringify(error.response?.data || error.message));
      setMedicos([]);
    }
  };

  const isAtivo = (m) => {
    return m?.ativo === true || m?.ativado === true || m?.enabled === true;
  };

  const ativarMedico = async (medico) => {
    if (!confirm("Deseja ativar este médico?")) return;
    try {
      const response = await api.put(`/medicos/${medico.id}/ativar`, {
        email: medico.username || medico.email
      }); 
      carregarMedicos();
      alert("Médico ativado com sucesso.");
    } catch (error) {
      alert("Erro ao ativar médico: " + JSON.stringify(error.response?.data || error.message));
    }
  };

  const inativarMedico = async (id) => {
    if (!confirm("Deseja inativar este médico?")) return;
    try {
      await api.delete(`/medicos/${id}`);
      carregarMedicos();
      alert("Médico inativado com sucesso.");
    } catch (error) {
      alert("Erro ao inativar médico: " + JSON.stringify(error.response?.data || error.message));
    }
  };

  return (
    <div className="container py-5">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="fw-bold text-primary">Gerenciar médicos</h2>
        <span className="badge bg-danger">Admin</span>
      </div>

      <div className="card shadow-sm border-0">
        <div className="card-body p-0">
          <div className="table-responsive" style={{ maxHeight: '65vh', overflowY: 'auto' }}>
            <table className="table table-hover mb-0">
              <thead className="table-light" style={{ position: 'sticky', top: 0, zIndex: 1 }}>
                <tr>
                  <th>Nome</th>
                  <th>E-mail</th>
                  <th>CRM</th>
                  <th>Especialidade</th>
                  <th>Cidade</th>
                  <th>Telefone</th>
                  <th>Status</th>
                  <th className="text-center">Ação</th>
                </tr>
              </thead>
              <tbody>
                {medicos.map((m) => (
                  <tr key={m.id}>
                    <td className="align-middle fw-semibold">{m.nome}</td>
                    <td className="align-middle">{m.username || m.email || "-"}</td>
                    <td className="align-middle">{m.crm || "-"}</td>
                    <td className="align-middle">{m.especialidade || "-"}</td>
                    <td className="align-middle">{m.endereco?.cidade || "-"}</td>
                    <td className="align-middle">{m.telefone || "-"}</td>
                    <td className="align-middle">
                      {isAtivo(m) ? (
                        <span className="badge bg-success">Ativo</span>
                      ) : (
                        <span className="badge bg-secondary">Inativo</span>
                      )}
                    </td>
                    <td className="text-center">
                      <div className="d-flex justify-content-center gap-2">
                        <button onClick={() => navigate(`/medicos/editar/${m.id}`)} className="btn btn-sm btn-info px-3 text-white">Editar</button>
                        {!isAtivo(m) ? (
                          <button onClick={() => ativarMedico(m)} className="btn btn-sm btn-primary px-3">Ativar</button>
                        ) : (
                          <button onClick={() => inativarMedico(m.id)} className="btn btn-sm btn-warning px-3">Inativar</button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default GerenciarMedicos;
