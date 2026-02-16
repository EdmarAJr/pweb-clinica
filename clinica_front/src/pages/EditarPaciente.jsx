import { useState, useEffect, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import { AuthContext } from "../context/AuthContext";

const maskPhone = (value) => {
  return value
    .replace(/\D/g, "")
    .replace(/(\d{2})(\d)/, "($1) $2")
    .replace(/(\d)(\d{4})$/, "$1-$2");
};

const maskCEP = (value) => {
  return value
    .replace(/\D/g, "")
    .replace(/(\d{5})(\d)/, "$1-$2")
    .replace(/(-\d{3})\d+?$/, "$1");
};

const EditarPaciente = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { hasRole } = useContext(AuthContext);
  const [loading, setLoading] = useState(true);
  const [showPassword, setShowPassword] = useState(false);

  const [formData, setFormData] = useState({
    nome: "",
    telefone: "",
    password: "",
    endereco: {
      logradouro: "",
      numero: "",
      complemento: "",
      cidade: "",
      estado: "",
      cep: ""
    }
  });

  useEffect(() => {
    const fetchPaciente = async () => {
      try {
        const response = await api.get(`/pacientes/${id}`);
        const data = response.data;
        
        setFormData({
            nome: data.nome || "",
            password: data.password || null,
            telefone: data.telefone || "",
            endereco: {
                logradouro: data.endereco?.logradouro || "",
                numero: data.endereco?.numero || "",
                complemento: data.endereco?.complemento || "",
                cidade: data.endereco?.cidade || "",
                estado: data.endereco?.estado || "",
                cep: data.endereco?.cep || "",
          },
        });
      } catch (error) {
        alert("Erro ao carregar dados do paciente:" + JSON.stringify(error.response?.data || error.message));
        navigate(hasRole("ROLE_ADMIN") ? "/pacientes" : "/consultas");
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchPaciente();
    }
  }, [id, navigate]);

  const handleChange = (e) => {
    let { name, value } = e.target;

    if (name === "telefone") value = maskPhone(value);

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleEnderecoChange = (e) => {
    let { name, value } = e.target;

    if (name === "cep") value = maskCEP(value);

    setFormData((prev) => ({
      ...prev,
      endereco: {
        ...prev.endereco,
        [name]: value,
      },
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.put(`/pacientes/${id}`, formData);
      alert("Paciente atualizado com sucesso!");
      navigate(hasRole("ROLE_ADMIN") ? "/pacientes" : "/consultas");
    } catch (error) {
      alert("Erro ao atualizar dados. Verifique as informações: " + JSON.stringify(error.response?.data || error.message));
    }
  };

  if (loading) {
    return <div className="container py-5 text-center">Carregando...</div>;
  }

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-md-8">
          <div className="card shadow-sm border-0 p-4">
            <h2 className="text-center fw-bold text-primary mb-4">Editar Paciente</h2>
            <form onSubmit={handleSubmit} className="row g-3">
              
              <h5 className="border-bottom pb-2 text-secondary">Dados Pessoais</h5>
              <div className="col-md-6">
                <label className="form-label">Nome completo</label>
                <input name="nome" className="form-control" value={formData.nome} onChange={handleChange} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Senha</label>
                <div className="input-group">
                  <input 
                    name="password" 
                    type={showPassword ? "text" : "password"} 
                    className="form-control" 
                    value={formData.password} 
                    onChange={handleChange} 
                  />
                  <button className="btn btn-outline-secondary" type="button" onClick={() => setShowPassword(!showPassword)}>
                    {showPassword ? (
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-eye-slash" viewBox="0 0 16 16">
                        <path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z"/>
                        <path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z"/>
                        <path d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12-.708.708z"/>
                      </svg>
                    ) : (
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-eye" viewBox="0 0 16 16">
                        <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z"/>
                        <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z"/>
                      </svg>
                    )}
                  </button>
                </div>
              </div>
              <div className="col-md-6">
                <label className="form-label">Telefone</label>
                <input name="telefone" className="form-control" value={formData.telefone} onChange={handleChange} required maxLength={15} />
              </div>

              <h5 className="border-bottom pb-2 text-secondary mt-4">Endereço</h5>
              <div className="col-md-8">
                <label className="form-label">Logradouro</label>
                <input name="logradouro" className="form-control" value={formData.endereco.logradouro} onChange={handleEnderecoChange} required />
              </div>
              <div className="col-md-4">
                <label className="form-label">Número</label>
                <input name="numero" className="form-control" value={formData.endereco.numero} onChange={handleEnderecoChange} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Complemento</label>
                <input name="complemento" className="form-control" value={formData.endereco.complemento} onChange={handleEnderecoChange} />
              </div>
              <div className="col-md-6">
                <label className="form-label">CEP</label>
                <input name="cep" className="form-control" value={formData.endereco.cep} onChange={handleEnderecoChange} required maxLength={9} />
              </div>
              <div className="col-md-8">
                <label className="form-label">Cidade</label>
                <input name="cidade" className="form-control" value={formData.endereco.cidade} onChange={handleEnderecoChange} required />
              </div>
              <div className="col-md-4">
                <label className="form-label">Estado</label>
                <input name="estado" className="form-control" value={formData.endereco.estado} onChange={handleEnderecoChange} required />
              </div>

              <div className="col-12 mt-4 d-flex gap-2">
                <button type="button" className="btn btn-secondary flex-grow-1" onClick={() => navigate(hasRole("ROLE_ADMIN") ? "/pacientes" : "/consultas")}>Cancelar</button>
                <button type="submit" className="btn btn-primary flex-grow-1">Salvar Alterações</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EditarPaciente;