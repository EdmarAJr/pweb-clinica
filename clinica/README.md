# Trabalho da disciplina Programação Web - INF012  2025 - 2
## 🏥 Sistema de clínica

## 📚 Categorias
- [Médicos](#médicos)
- [Pacientes](#pacientes)
- [Consultas](#consultas)

---

## 👨‍⚕️ Médicos

### 🩺 Cadastro de Médicos
**Etiquetas:** Testado | Médicos  
**Descrição:**  
O sistema deve permitir o **cadastro de médicos**, exigindo o preenchimento dos seguintes campos:

- Nome
- E-mail
- Telefone
- CRM
- Especialidade *(Ortopedia, Cardiologia, Ginecologia ou Dermatologia)*
- Endereço completo *(logradouro, número, complemento, bairro, cidade, UF e CEP)*

> Todas as informações são obrigatórias, **exceto número e complemento** do endereço.

---

### 📋 Listagem de Médicos
**Etiquetas:** Testado | Médicos  
**Descrição:**  
O sistema deve permitir a **listagem de médicos cadastrados**, exibindo:

- Nome
- E-mail
- CRM
- Especialidade

> A listagem deve ser **ordenada pelo nome** (crescente) e **paginada**, exibindo **10 registros por página**.

---

### ✏️ Atualização de Médicos
**Etiquetas:** Testado | Médicos  
**Descrição:**  
O sistema deve permitir a **atualização de dados cadastrais** dos médicos.  
Campos que podem ser atualizados:

- Nome
- Telefone
- Endereço

**Restrições de atualização:**
- ❌ O **e-mail** não pode ser alterado
- ❌ O **CRM** não pode ser alterado
- ❌ A **especialidade** não pode ser alterada

---

### ❌ Exclusão de Médicos
**Etiquetas:** Testado | Médicos  
**Descrição:**  
O sistema deve permitir a **exclusão de médicos cadastrados**, respeitando as seguintes regras:

- A exclusão **não deve apagar os dados** do médico.
- O médico deve ser marcado como **“inativo”** no sistema.

---

## 🧑‍🦰 Pacientes

### 🩺 Cadastro de Pacientes
**Etiquetas:** Testado | Pacientes  
**Descrição:**  
O sistema deve permitir o **cadastro de pacientes**, exigindo o preenchimento dos seguintes campos:

- Nome
- E-mail
- Telefone
- CPF
- Endereço completo *(logradouro, número, complemento, bairro, cidade, UF e CEP)*

> Todas as informações são obrigatórias, **exceto número e complemento** do endereço.

---

### 📋 Listagem de Pacientes
**Etiquetas:** Testado | Pacientes  
**Descrição:**  
O sistema deve permitir a **listagem de pacientes cadastrados**, exibindo:

- Nome
- E-mail
- CPF

> A listagem deve ser **ordenada pelo nome** (crescente) e **paginada**, com **10 registros por página**.

---

### ✏️ Atualização de Pacientes
**Etiquetas:** Testado | Pacientes  
**Descrição:**  
O sistema deve permitir a **atualização de dados cadastrais** dos pacientes.  
Campos que podem ser atualizados:

- Nome
- Telefone
- Endereço

**Restrições de atualização:**
- ❌ O **e-mail** não pode ser alterado
- ❌ O **CPF** não pode ser alterado

---

### ❌ Exclusão de Pacientes
**Etiquetas:** Testado | Pacientes  
**Descrição:**  
O sistema deve permitir a **exclusão de pacientes cadastrados**, respeitando as seguintes regras:

- A exclusão **não deve apagar os dados** do paciente.
- O paciente deve ser marcado como **“inativo”** no sistema.

---

## 📅 Consultas

### 🕐 Marcação de Consultas
**Etiquetas:** Testado | Consultas  
**Descrição:**  
O sistema deve permitir o **agendamento de consultas**, exigindo o preenchimento dos seguintes campos:

- Paciente
- Médico
- Data/Hora da consulta

**Regras de negócio:**
- Horário de funcionamento: **segunda a sábado, das 07:00 às 19:00**
- Duração fixa: **1 hora por consulta**
- Agendamento com **mínimo de 30 minutos de antecedência**
- ❌ Não permitir agendamento com **pacientes inativos**
- ❌ Não permitir agendamento com **médicos inativos**
- ❌ Não permitir **mais de uma consulta no mesmo dia para o mesmo paciente**
- ❌ Não permitir **médico com conflito de horário** (outra consulta na mesma data/hora)
- 🩺 A escolha do médico é **opcional** — se não for informada, o sistema deve **atribuir aleatoriamente** um médico disponível.

---

### 🚫 Cancelamento de Consultas
**Etiquetas:** Testado | Consultas  
**Descrição:**  
O sistema deve permitir o **cancelamento de consultas**, exigindo os campos:

- Consulta
- Motivo do cancelamento

**Regras de negócio:**
- O motivo é **obrigatório**, devendo ser uma das opções:
    - Paciente desistiu
    - Médico cancelou
    - Outros
- A consulta **só pode ser cancelada com antecedência mínima de 24 horas**.