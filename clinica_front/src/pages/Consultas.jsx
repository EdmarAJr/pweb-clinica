import { useEffect, useState, useContext } from "react";
import api from "../services/api";
import { AuthContext } from "../context/AuthContext";

const getHorariosDisponiveis = (dataStr) => {
  if (!dataStr) return [];
  const [ano, mes, dia] = dataStr.split('-').map(Number);
  const data = new Date(ano, mes - 1, dia);
  const diaSemana = data.getDay(); 

  if (diaSemana === 0) return []; 

  const horarios = [];
  const horaFim = diaSemana === 6 ? 12 : 19;
  
  for (let h = 8; h <= horaFim; h++) {
    horarios.push(`${h.toString().padStart(2, '0')}:00`);
  }
  return horarios;
};

const Consultas = () => {
  const [consultas, setConsultas] = useState([]);
  const [medicos, setMedicos] = useState([]);
  const { hasRole, user } = useContext(AuthContext);  
  const [idMedico, setIdMedico] = useState("");
  const [agendarData, setAgendarData] = useState("");
  const [agendarHora, setAgendarHora] = useState("");
  const [motivo, setMotivo] = useState("");
  const [descricao, setDescricao] = useState("");

  const [showCancelModal, setShowCancelModal] = useState(false);
  const [cancelId, setCancelId] = useState(null);
  const [motivoCancelamento, setMotivoCancelamento] = useState("OUTROS");
  const [descricaoCancelamento, setDescricaoCancelamento] = useState("");
  const [statusFilter, setStatusFilter] = useState("TODAS");
  const [searchTerm, setSearchTerm] = useState("");
  const [medicoFilter, setMedicoFilter] = useState("");
  const [editMedicoFilter, setEditMedicoFilter] = useState("");

  const [showEditModal, setShowEditModal] = useState(false);
  const [editId, setEditId] = useState(null);
  const [editIdMedico, setEditIdMedico] = useState("");
  const [editData, setEditData] = useState("");
  const [editHora, setEditHora] = useState("");

  useEffect(() => {
    carregarConsultas();
    carregarMedicos();
  }, []);

  const carregarMedicos = async () => {
    try {
      const response = await api.get("/medicos/listar");
      setMedicos(Array.isArray(response.data) ? response.data : response.data.content || []);
    } catch (error) {
      alert("Erro ao carregar médicos: " + JSON.stringify(error.response?.data || error.message));
    }
  };

  const carregarConsultas = async () => {
    try {
      const response = await api.get("/consultas/listar?page=0&size=1000");
      const dados = Array.isArray(response.data) ? response.data : (response.data?.content || []);
      
      if (hasRole("ROLE_ADMIN")) {
        setConsultas(dados);
      } else {
        const ativas = dados.filter(c => c.status !== "CANCELADA");
        setConsultas(ativas);
      }
    } catch (error) {
      alert("Erro ao buscar consultas: " + JSON.stringify(error.response?.data || error.message));
    }
  };

  const agendarConsulta = async (e) => {
    e.preventDefault();
    try {
      const dataHora = `${agendarData}T${agendarHora}`;
      await api.post("/consultas/agendar", { 
        idMedico, 
        dataHora, 
        descricao,
        idPaciente: user.id,
        emailPaciente: user.sub
      });
      alert("Consulta agendada!");
      setIdMedico("");
      setAgendarData("");
      setAgendarHora("");
      setDescricao("");
      carregarConsultas();
    } catch (error) {
      alert(JSON.stringify(error.response?.data || error));
    }
  };

  const confirmarCancelamento = async () => {
    try {
      await api.delete(`/consultas/${cancelId}`, {
        data: { 
          motivoCancelamento: motivoCancelamento, 
          descricaoCancelamento: descricaoCancelamento 
        }
      });
      alert("Consulta cancelada com sucesso!");
      setShowCancelModal(false);
      carregarConsultas(); 
    } catch (error) {
      alert("Erro no cancelamento :" + JSON.stringify(error.response?.data || error.message));
    }
  };

  const abrirModalEdicao = (consulta) => {
    setEditId(consulta.id);
    setEditIdMedico(consulta.medico?.id || consulta.idMedico || "");
    
    if (consulta.dataHora) {
      const dt = new Date(consulta.dataHora);
      const yyyy = dt.getFullYear();
      const mm = String(dt.getMonth() + 1).padStart(2, '0');
      const dd = String(dt.getDate()).padStart(2, '0');
      setEditData(`${yyyy}-${mm}-${dd}`);
      const hh = String(dt.getHours()).padStart(2, '0');
      setEditHora(`${hh}:00`);
    } else {
      setEditData("");
      setEditHora("");
    }
    setEditMedicoFilter("");
    setShowEditModal(true);
  };

  const salvarEdicao = async () => {
    try {
      const dataHora = `${editData}T${editHora}`;
      await api.put(`/consultas/reagendar/${editId}`, { 
        idMedico: editIdMedico, 
        dataHora,
        idPaciente: user.id,
        emailPaciente: user.sub
      });
      alert("Consulta atualizada com sucesso!");
      setShowEditModal(false);
      carregarConsultas();
    } catch (error) {
      alert(JSON.stringify(error.response?.data || error));
    }
  };

  const getNomeMedico = (c) => c.nomeMedico || c.medico?.nome;
  const getNomePaciente = (c) => c.nomePaciente || c.paciente?.nome;
  const getEmailPaciente = (c) => c.emailPaciente || c.paciente?.username;
  const getDescricao = (c) => c.descricao || c.descricao?.descricao ||"Sem descrição";
  const getMotivoCancelamento = (c) => c.motivoCancelamento || c.motivoCancelamento?.motivo;
  const getDescricaoCancelamento = (c) => c.descricaoCancelamento || c.descricaoCancelamento?.descricao;


  const consultasFiltradas = consultas.filter(c => {
    if (hasRole("ROLE_ADMIN") && statusFilter !== "TODAS" && c.status !== statusFilter) {
      return false;
    }

    if (searchTerm) {
      const term = searchTerm.toLowerCase();
      const nomeMedico = (c.nomeMedico || c.medico?.nome || "").toLowerCase();

      const medicoObj = medicos.find(m => m.id === (c.medico?.id || c.idMedico));
      const especialidade = (c.medico?.especialidade || medicoObj?.especialidade || "").toLowerCase();

      if (!nomeMedico.includes(term) && !especialidade.includes(term)) return false;
    }
    return true;
  });

  const filtrarMedicos = (lista, filtro) => {
    if (!filtro) return lista;
    const termo = filtro.toLowerCase();
    return lista.filter(m => 
      m.nome.toLowerCase().includes(termo) || 
      (m.especialidade && m.especialidade.toLowerCase().includes(termo))
    );
  };

  return (
    <div className="container py-5">
      <h2 className="fw-bold text-primary mb-4">Gestão de consultas</h2>

      {hasRole("ROLE_PACIENTE") && (
        <div className="card shadow-sm border-0 mb-5">
          <div className="card-header bg-primary text-white fw-bold">Agendar consulta</div>
          <div className="card-body">
            <form onSubmit={agendarConsulta} className="row g-3">
              <div className="col-md-3">
                <input 
                  type="text" 
                  className="form-control mb-1" 
                  placeholder="Buscar médico..." 
                  value={medicoFilter} 
                  onChange={e => setMedicoFilter(e.target.value)} 
                />
                <select className="form-select" value={idMedico} onChange={e => setIdMedico(e.target.value)} required>
                  <option value="">Selecione o médico</option>
                  {filtrarMedicos(medicos, medicoFilter).map(m => <option key={m.id} value={m.id}>{m.nome} - {m.especialidade}</option>)}
                </select>
              </div>
              <div className="col-md-2">
                <input type="date" className="form-control" value={agendarData} onChange={e => setAgendarData(e.target.value)} required />
              </div>
              <div className="col-md-2">
                <select className="form-select" value={agendarHora} onChange={e => setAgendarHora(e.target.value)} required>
                  <option value="">Horário</option>
                  {getHorariosDisponiveis(agendarData).map(h => <option key={h} value={h}>{h}</option>)}
                </select>
              </div>
              <div className="col-md-3">
                <input type="text" className="form-control" placeholder="Informe o motivo da consulta" value={descricao} onChange={e => setDescricao(e.target.value)} required/>
              </div>
              <div className="col-md-2">
                <button type="submit" className="btn btn-success w-100">Agendar</button>
              </div>
            </form>
          </div>
        </div>
      )}
      
      {hasRole("ROLE_ADMIN")  && (
        <div className="card shadow-sm border-0 mb-4 p-3">
          <div className="row g-3 align-items-center">
            {hasRole("ROLE_ADMIN") && (
              <div className="col-auto d-flex align-items-center gap-2">
                <label className="fw-bold">Status:</label>
                <select className="form-select w-auto" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                  <option value="TODAS">Todas</option>
                  <option value="AGENDADA">Agendada</option>
                  <option value="CANCELADA">Cancelada</option>
                  <option value="CONCLUíDA">Realizada</option>
                </select>
              </div>
            )}
            <div className="col">
              <input 
                type="text" 
                className="form-control" 
                placeholder="Buscar por médico ou especialidade..." 
                value={searchTerm} 
                onChange={(e) => setSearchTerm(e.target.value)} 
              />
            </div>
          </div>
        </div>
      )}

      <div className="card shadow-sm border-0">
        <div className="table-responsive">
          <table className="table table-hover mb-0">
            <thead className="table-light">
              <tr>
                <th>Médico</th>
                <th>Paciente</th>
                <th>Descricão</th>
                <th>Data e hora</th>
                <th>Status</th>
                {hasRole("ROLE_ADMIN") && <th>Motivo Cancelamento</th>}
                {hasRole("ROLE_ADMIN") && <th>Descrição Cancelamento.</th>}
                <th className="text-center">Ações</th>
              </tr>
            </thead>
            <tbody>
              {consultasFiltradas.map((c) => (
                <tr key={c.id}>
                  <td className="align-middle fw-semibold">{getNomeMedico(c)}</td>
                  <td className="align-middle">{getNomePaciente(c)}</td>
                  <td className="align-middle">{getDescricao(c)}</td>
                  <td className="align-middle">{new Date(c.dataHora).toLocaleString('pt-BR')}</td>
                  <td className="align-middle">
                    <span className={`badge ${c.status === 'CANCELADA' ? 'bg-danger' : c.status === 'CONCLUíDA' ? 'bg-success' : 'bg-primary'}`}>
                      {c.status}
                    </span>
                  </td>
                  {hasRole("ROLE_ADMIN") && <td className="align-middle">{getMotivoCancelamento(c) || "-"}</td>}
                  {hasRole("ROLE_ADMIN") && <td className="align-middle">{getDescricaoCancelamento(c) || "-"}</td>}
                  <td className="text-center">
                    {hasRole("ROLE_PACIENTE") && (
                    <button 
                      onClick={() => abrirModalEdicao(c)} 
                      className="btn btn-outline-primary btn-sm px-3 me-2"
                    >
                      Reagendar consulta
                    </button>)}
                    <button 
                      onClick={() => { setCancelId(c.id); setShowCancelModal(true); }} 
                      className="btn btn-outline-danger btn-sm px-3"
                    >
                      Cancelar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {showCancelModal && (
        <div className="modal d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog">
            <div className="modal-content border-0 shadow">
              <div className="modal-header bg-danger text-white">
                <h5 className="modal-title">Confirmar cancelamento</h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => setShowCancelModal(false)}></button>
              </div>
              <div className="modal-body">
                <label className="form-label fw-bold">Motivo do cancelamento</label>
                <select 
                  className="form-select mb-3" 
                  value={motivoCancelamento} 
                  onChange={e => setMotivoCancelamento(e.target.value)}
                >

                  {hasRole("ROLE_PACIENTE") && (
                    <option value="DESISTÊNCIA">Paciente desistiu</option>
                  )}
                  <option value="OUTROS">Outros</option>
                  {hasRole("ROLE_MEDICO") && (
                  <option value="CANCELAMENTO">Médico cancelou</option>
                  )}
                </select>
                <label className="form-label fw-bold">Descrição</label>
                <textarea 
                  className="form-control" 
                  rows="3" 
                  value={descricaoCancelamento} 
                  onChange={e => setDescricaoCancelamento(e.target.value)}
                  placeholder="Justifique o cancelamento..."
                />
              </div>
              <div className="modal-footer">
                <button className="btn btn-light" onClick={() => setShowCancelModal(false)}>Voltar</button>
                <button className="btn btn-danger" onClick={confirmarCancelamento}>Confirmar Cancelamento</button>
              </div>
            </div>
          </div>
        </div>
      )}

      {showEditModal && (
        <div className="modal d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog">
            <div className="modal-content border-0 shadow">
              <div className="modal-header bg-primary text-white">
                <h5 className="modal-title">Editar consulta</h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => setShowEditModal(false)}></button>
              </div>
              <div className="modal-body">
                <label className="form-label fw-bold">Médico</label>
                <input 
                  type="text" 
                  className="form-control mb-1" 
                  placeholder="Buscar médico..." 
                  value={editMedicoFilter} 
                  onChange={e => setEditMedicoFilter(e.target.value)} 
                />
                <select className="form-select mb-3" value={editIdMedico} onChange={e => setEditIdMedico(e.target.value)}>
                  <option value="">Selecione o Médico</option>
                  {filtrarMedicos(medicos, editMedicoFilter).map(m => <option key={m.id} value={m.id}>{m.nome} - {m.especialidade}</option>)}
                </select>
                
                <div className="row">
                  <div className="col-6">
                    <label className="form-label fw-bold">Data</label>
                    <input type="date" className="form-control" value={editData} onChange={e => setEditData(e.target.value)} />
                  </div>
                  <div className="col-6">
                    <label className="form-label fw-bold">Horário</label>
                    <select className="form-select" value={editHora} onChange={e => setEditHora(e.target.value)}>
                      <option value="">Selecione</option>
                      {getHorariosDisponiveis(editData).map(h => <option key={h} value={h}>{h}</option>)}
                    </select>
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <button className="btn btn-light" onClick={() => setShowEditModal(false)}>Cancelar</button>
                <button className="btn btn-primary" onClick={salvarEdicao}>Salvar alterações</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Consultas;