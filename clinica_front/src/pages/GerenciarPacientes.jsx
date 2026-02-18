import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

const GerenciarPacientes = () => {
	const [pacientes, setPacientes] = useState([]);
	const navigate = useNavigate();

	useEffect(() => {
		carregarPacientes();
	}, []);

	const carregarPacientes = async () => {
		try {
			const response = await api.get("/pacientes/listar");
			setPacientes(Array.isArray(response.data) ? response.data : response.data.content || []);
		} catch (error) {
			console.error("Erro ao carregar pacientes:", error);
			setPacientes([]);
		}
	};

	const deletarPaciente = async (id) => {
		if (!confirm("Deseja deletar este paciente?")) return;
		try {
			await api.delete(`/pacientes/${id}`);
			setPacientes((prev) => prev.filter((p) => p.id !== id));
			alert("Paciente deletado com sucesso.");
		} catch (error) {
			console.error("Erro ao deletar paciente:", error);
			alert("Erro ao deletar paciente.");
		}
	};

	return (
		<div className="container py-5">
			<div className="d-flex justify-content-between align-items-center mb-4">
				<h2 className="fw-bold text-primary">Gerenciar pacientes</h2>
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
									<th>CPF</th>
									<th>Cidade</th>
									<th>Telefone</th>
									<th className="text-center">Ação</th>
								</tr>
							</thead>
							<tbody>
								{pacientes.map((p) => (
									<tr key={p.id}>
										<td className="align-middle fw-semibold">{p.nome}</td>
										<td className="align-middle">{p.username || p.email || "-"}</td>
										<td className="align-middle">{p.cpf || "-"}</td>
										<td className="align-middle">{p.endereco?.cidade || "-"}</td>
										<td className="align-middle">{p.telefone || "-"}</td>
										<td className="text-center">
											<div className="d-flex justify-content-center gap-2">
												<button onClick={() => navigate(`/pacientes/editar/${p.id}`)} className="btn btn-sm btn-info px-3 text-white">Editar</button>
												<button onClick={() => deletarPaciente(p.id)} className="btn btn-sm btn-danger px-3">Deletar</button>
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

export default GerenciarPacientes;
