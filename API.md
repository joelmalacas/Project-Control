# API de Projetos

Documentação dos endpoints disponibilizados pelo `ProjectApiController`.

A API permite criar, consultar, atualizar e remover projetos, bem como consultar e alterar o seu estado e respetivo histórico.

## Índice

1. [Endpoints de Consulta (GET)](#1-endpoints-de-consulta-get)
2. [Endpoints de Criação (POST)](#2-endpoints-de-criação-post)
3. [Endpoints de Atualização (PUT)](#3-endpoints-de-atualização-put)
4. [Endpoints de Remoção (DELETE)](#4-endpoints-de-remoção-delete)

---

## 1. Endpoints de Consulta (GET)

### 1.1 Listar Todos os Projetos

Retorna todos os projetos registados na base de dados.

**Endpoint**

```http
GET /api/projects
```

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | Lista de projetos. |

---

### 1.2 Obter Assinatura por ID

Retorna a assinatura associada a um projeto.

**Endpoint**

```http
GET /api/projects/{id}/signature
```

**Parâmetros**

| Parâmetro | Tipo | Localização | Obrigatório | Descrição |
|---|---|---|---|---|
| `id` | `Long` | Path | Sim | Identificador do projeto. |

**Exemplo**

```http
GET /api/projects/1/signature
```

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | Retorna a assinatura do projeto. |
| `404 Not Found` | `"Projeto não encontrado"` |

---

### 1.3 Obter Projeto por Assinatura

Retorna os dados completos do projeto correspondente à assinatura fornecida.

**Endpoint**

```http
GET /api/projects/{sign}/id
```

**Parâmetros**

| Parâmetro | Tipo | Localização | Obrigatório | Descrição |
|---|---|---|---|---|
| `sign` | `String` | Path | Sim | Assinatura do projeto. |

**Exemplo**

```http
GET /api/projects/a1b2c3d4e5f6/id
```

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | Retorna o objeto do projeto. |
| `404 Not Found` | `"Projeto não encontrado"` |

---

### 1.4 Obter Assinatura por Nome

Procura um projeto pelo nome e retorna a sua assinatura.

**Endpoint**

```http
GET /api/projects/by-name/{name}/signature
```

**Parâmetros**

| Parâmetro | Tipo | Localização | Obrigatório | Descrição |
|---|---|---|---|---|
| `name` | `String` | Path | Sim | Nome do projeto. |

**Exemplo**

```http
GET /api/projects/by-name/meu-projeto/signature
```

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | Retorna a assinatura do projeto. |
| `400 Bad Request` | O nome está vazio ou em branco. |
| `404 Not Found` | `"Projeto não encontrado"` |

---

### 1.5 Obter Estado por Assinatura

Retorna o estado atual de um projeto através da sua assinatura.

**Endpoint**

```http
GET /api/projects/by-signature/{sign}/status
```

**Parâmetros**

| Parâmetro | Tipo | Localização | Obrigatório | Descrição |
|---|---|---|---|---|
| `sign` | `String` | Path | Sim | Assinatura do projeto. |

**Exemplo**

```http
GET /api/projects/by-signature/a1b2c3d4e5f6/status
```

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | Retorna o estado atual do projeto. |
| `400 Bad Request` | A assinatura está vazia ou em branco. |
| `404 Not Found` | `"Projeto não encontrado"` |

---

### 1.6 Obter Estado por ID

Retorna o estado atual de um projeto através do seu ID.

**Endpoint**

```http
GET /api/projects/{id}/status
```

**Parâmetros**

| Parâmetro | Tipo | Localização | Obrigatório | Descrição |
|---|---|---|---|---|
| `id` | `Long` | Path | Sim | Identificador do projeto. |

**Exemplo**

```http
GET /api/projects/1/status
```

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | Retorna o estado do projeto. |
| `404 Not Found` | `"Projeto não encontrado"` |

---

### 1.7 Obter Estado por Nome

Retorna o estado atual de um projeto através do seu nome.

**Endpoint**

```http
GET /api/projects/by-name/{name}/status
```

**Parâmetros**

| Parâmetro | Tipo | Localização | Obrigatório | Descrição |
|---|---|---|---|---|
| `name` | `String` | Path | Sim | Nome do projeto. |

**Exemplo**

```http
GET /api/projects/by-name/meu-projeto/status
```

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | Retorna o estado do projeto. |
| `400 Bad Request` | O nome está vazio ou em branco. |
| `404 Not Found` | `"Projeto não encontrado"` |

---

### 1.8 Obter Projeto por ID

Retorna todos os dados de um projeto específico.

**Endpoint**

```http
GET /api/projects/{id}
```

**Parâmetros**

| Parâmetro | Tipo | Localização | Obrigatório | Descrição |
|---|---|---|---|---|
| `id` | `Long` | Path | Sim | Identificador do projeto. |

**Exemplo**

```http
GET /api/projects/1
```

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | Retorna o objeto do projeto. |
| `404 Not Found` | `"Projeto não encontrado"` |

---

### 1.9 Listar Histórico do Projeto

Retorna o histórico de alterações de estado de um projeto.

**Endpoint**

```http
GET /api/projects/{id}/history
```

**Parâmetros**

| Parâmetro | Tipo | Localização | Obrigatório | Descrição |
|---|---|---|---|---|
| `id` | `Long` | Path | Sim | Identificador do projeto. |

**Exemplo**

```http
GET /api/projects/1/history
```

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | Retorna uma lista de `ProjectHistory`. |
| `404 Not Found` | `"Projeto não encontrado"` |

---

## 2. Endpoints de Criação (POST)

### 2.1 Criar Projeto

Cria um novo projeto.

A API gera automaticamente uma assinatura única com **40 caracteres**. Caso o estado não seja informado, o projeto é criado com o estado `ACTIVE`.

**Endpoint**

```http
POST /api/projects
```

**Content-Type**

```http
Content-Type: application/json
```

**Body**

O corpo do pedido deve conter um objeto `Project`.

```json
{
  "name": "Meu Projeto",
  "description": "Descrição do projeto",
  "url_repo": "https://github.com/example/project",
  "url_prod": "https://example.com",
  "userId": 1
}
```

> Os restantes campos dependem da estrutura da entidade `Project`.

**Regras**

- `userId` é obrigatório.
- A assinatura é gerada automaticamente.
- A assinatura possui 40 caracteres.
- O estado inicial é `ACTIVE` quando não for fornecido.

**Respostas**

| Código | Descrição |
|---|---|
| `201 Created` | Projeto criado com sucesso. |
| `400 Bad Request` | `userId` em falta ou dados inválidos. |

---

## 3. Endpoints de Atualização (PUT)

### 3.1 Atualizar Campos do Projeto

Atualiza parcialmente os dados de um projeto.

Os campos suportados são:

- `name`
- `description`
- `url_repo`
- `url_prod`

**Endpoint**

```http
PUT /api/projects/{id}
```

**Parâmetros**

| Parâmetro | Tipo | Localização | Obrigatório | Descrição |
|---|---|---|---|---|
| `id` | `Long` | Path | Sim | Identificador do projeto. |

**Content-Type**

```http
Content-Type: application/json
```

**Body**

```json
{
  "name": "Novo nome",
  "description": "Nova descrição",
  "url_repo": "https://github.com/example/new-project",
  "url_prod": "https://example.com"
}
```

É possível enviar apenas os campos que se pretende atualizar.

**Exemplo**

```json
{
  "name": "Novo nome"
}
```

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | Retorna o projeto atualizado. |
| `400 Bad Request` | Um dos campos enviados está vazio ou em branco. |
| `404 Not Found` | `"Projeto não encontrado"` |

---

### 3.2 Gerar Nova Assinatura

Gera uma nova assinatura única para o projeto, substituindo a assinatura anterior.

**Endpoint**

```http
PUT /api/projects/{id}/generateSign
```

**Parâmetros**

| Parâmetro | Tipo | Localização | Obrigatório | Descrição |
|---|---|---|---|---|
| `id` | `Long` | Path | Sim | Identificador do projeto. |

**Exemplo**

```http
PUT /api/projects/1/generateSign
```

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | Retorna o projeto com a nova assinatura. |
| `404 Not Found` | `"Projeto não foi encontrado"` |

---

### 3.3 Atualizar Estado do Projeto

Altera o estado de um projeto e regista automaticamente a alteração no histórico.

**Endpoint**

```http
PUT /api/projects/{id}/updateStatus
```

**Parâmetros**

| Parâmetro | Tipo | Localização | Obrigatório | Descrição |
|---|---|---|---|---|
| `id` | `Long` | Path | Sim | Identificador do projeto. |

**Headers**

| Header | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `X-User-Id` | `Long` | Não | ID do utilizador que efetua a alteração. |

**Content-Type**

```http
Content-Type: application/json
```

**Body**

```json
{
  "status": "ACTIVE"
}
```

O valor de `status` deve corresponder a um valor válido do enum `ProjectStateEnum`.

**Exemplo**

```http
PUT /api/projects/1/updateStatus
X-User-Id: 10
Content-Type: application/json

{
  "status": "INACTIVE"
}
```

**Comportamento**

1. Valida o estado recebido.
2. Converte o valor para `ProjectStateEnum`.
3. Atualiza o estado do projeto.
4. Regista a alteração no histórico do projeto.

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | Retorna o projeto com o estado atualizado. |
| `400 Bad Request` | Estado em falta, vazio ou inválido. |
| `404 Not Found` | `"Projeto não encontrado"` |

---

## 4. Endpoints de Remoção (DELETE)

### 4.1 Eliminar Projeto

Remove permanentemente um projeto da base de dados.

**Endpoint**

```http
DELETE /api/projects/{id}
```

**Parâmetros**

| Parâmetro | Tipo | Localização | Obrigatório | Descrição |
|---|---|---|---|---|
| `id` | `Long` | Path | Sim | Identificador do projeto. |

**Exemplo**

```http
DELETE /api/projects/1
```

**Respostas**

| Código | Descrição |
|---|---|
| `200 OK` | `"Projeto excluído com sucesso"` |
| `404 Not Found` | `"Projeto não existe"` |

---

## Resumo dos Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/projects` | Lista todos os projetos |
| `GET` | `/api/projects/{id}` | Obtém um projeto por ID |
| `GET` | `/api/projects/{id}/signature` | Obtém a assinatura por ID |
| `GET` | `/api/projects/{id}/status` | Obtém o estado por ID |
| `GET` | `/api/projects/{id}/history` | Obtém o histórico do projeto |
| `GET` | `/api/projects/{sign}/id` | Obtém um projeto por assinatura |
| `GET` | `/api/projects/by-name/{name}/signature` | Obtém a assinatura por nome |
| `GET` | `/api/projects/by-name/{name}/status` | Obtém o estado por nome |
| `GET` | `/api/projects/by-signature/{sign}/status` | Obtém o estado por assinatura |
| `POST` | `/api/projects` | Cria um projeto |
| `PUT` | `/api/projects/{id}` | Atualiza os campos do projeto |
| `PUT` | `/api/projects/{id}/generateSign` | Gera uma nova assinatura |
| `PUT` | `/api/projects/{id}/updateStatus` | Atualiza o estado do projeto |
| `DELETE` | `/api/projects/{id}` | Remove um projeto |

---

## Estados do Projeto

O estado do projeto é representado pelo enum `ProjectStateEnum`.

Exemplo de utilização:

```json
{
  "status": "ACTIVE"
}
```

> Os valores disponíveis devem corresponder aos valores definidos no `ProjectStateEnum` da aplicação.

---

## Formato Geral dos Pedidos

Para endpoints que recebem JSON, utilizar:

```http
Content-Type: application/json
```

Exemplo:

```http
POST /api/projects
Content-Type: application/json

{
  "name": "Meu Projeto",
  "description": "Descrição",
  "url_repo": "https://github.com/example/project",
  "url_prod": "https://example.com",
  "userId": 1
}
```

## Códigos HTTP Utilizados

| Código | Significado |
|---|---|
| `200 OK` | Operação realizada com sucesso |
| `201 Created` | Recurso criado com sucesso |
| `400 Bad Request` | Pedido inválido ou dados incorretos |
| `404 Not Found` | Recurso não encontrado |

---
