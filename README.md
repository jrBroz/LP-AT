[README.md](https://github.com/user-attachments/files/29363643/README.md)
# 🐾 PetFriends — Sistema de Microsserviços Event-Driven

Projeto de avaliação acadêmica que implementa uma arquitetura de **microsserviços event-driven** com **DDD (Domain-Driven Design)**, **RabbitMQ** como message broker e **PostgreSQL** como banco de dados de cada serviço.

---

## 📐 Arquitetura

```
[ Cliente / Postman ]
        |
        | POST /api/pedidos  (REST síncrono)
        ▼
┌─────────────────────────────┐
│   MS Pedidos  (:8080)       │  → publica → [petfriends.exchange]
│   - Pedido (entity)         │                    |
│   - StatusPedido (enum)     │     routing key: "pedido.criado"
│   - PedidoRepository        │                    |
└─────────────────────────────┘                    ▼
        ▲                             ┌──────────────────────────┐
        │ pedido.status.update        │  MS Almoxarifado (:8081) │
        │ (EM_SEPARACAO / SEPARADO)   │  - OrdemServico (entity)  │
        │                             │  - EnderecoEstoque (VO)   │
        │                             └──────────────────────────┘
        │                                          |
        │                           routing key: "pedido.separado"
        │                                          |
        │                                          ▼
        │                             ┌──────────────────────────┐
        │ pedido.status.update        │  MS Transporte   (:8082) │
        └─────────────────────────────│  - Entrega (entity)       │
             (EM_ROTA)                │  - DadosDestino (VO)      │
                                      └──────────────────────────┘
```

### Fluxo completo de um pedido

| Passo | Quem age | Evento publicado | Status resultante |
|-------|----------|------------------|-------------------|
| 1 | Cliente (POST REST) | — | `CRIADO` |
| 2 | Almoxarifado consome | `pedido.status.update` → SEPARADO | `SEPARADO` |
| 3 | Almoxarifado publica | `pedido.separado` | — |
| 4 | Transporte consome | `pedido.status.update` → EM_ROTA | `EM_ROTA` |

---

## 🛠 Tecnologias

| Tecnologia | Versão | Função |
|------------|--------|--------|
| Java | 17 | Linguagem |
| Spring Boot | 3.1.5 | Framework principal |
| Spring AMQP | (incluso) | Integração com RabbitMQ |
| Spring Data JPA | (incluso) | Persistência |
| PostgreSQL | 15 | Banco de dados (1 por serviço) |
| RabbitMQ | 3 + Management | Message Broker |
| Docker / Docker Compose | — | Containerização |
| Maven | 3.9 | Build (multi-stage no Dockerfile) |

---

## 🗂 Estrutura de Pastas

```
At_LP_Final/
├── docker-compose.yml
├── docs/
│   ├── Exercicios_PetFriends.md
│   └── lp-at-img1/2/3.jpg
│
├── PetFriends_Pedidos/          ← Microsserviço de Pedidos (porta 8080)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/petfriends/pedidos/
│       ├── PedidosApplication.java
│       ├── controller/PedidoController.java
│       ├── domain/entity/Pedido.java
│       ├── domain/repository/PedidoRepository.java
│       ├── service/PedidoService.java
│       └── config/RabbitMQConfig.java
│
├── PetFriends_Almoxarifado/     ← Microsserviço de Almoxarifado (porta 8081)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/petfriends/almoxarifado/
│       ├── AlmoxarifadoApplication.java
│       ├── domain/entity/OrdemServicoAlmoxarifado.java
│       ├── domain/valueobject/EnderecoEstoque.java
│       ├── domain/repository/OrdemServicoRepository.java
│       ├── service/PedidoCriadoListener.java
│       └── config/RabbitMQConfig.java
│
└── PetFriends_Transporte/       ← Microsserviço de Transporte (porta 8082)
    ├── Dockerfile
    ├── pom.xml
    └── src/main/java/com/petfriends/transporte/
        ├── TransporteApplication.java
        ├── domain/entity/Entrega.java
        ├── domain/valueobject/DadosDestino.java
        ├── domain/repository/EntregaRepository.java
        ├── service/PedidoSeparadoListener.java
        └── config/RabbitMQConfig.java
```

---

## 🚀 Como Subir o Projeto (Roadmap de Apresentação)

### Pré-requisitos
- Docker Desktop instalado e em execução
- Porta 8080, 8081, 8082, 5432, 5433, 5434, 5672, 15672 livres

---

### Passo 1 — Entrar na pasta raiz do projeto

```bash
cd At_LP_Final
```

---

### Passo 2 — Subir todos os serviços com Docker Compose

```bash
docker compose up --build
```

> O `--build` força a compilação das imagens Maven na primeira vez.  
> Isso pode levar **3 a 5 minutos** na primeira execução (Maven baixa dependências).
>
> Para execuções seguintes (sem mudança de código):
> ```bash
> docker compose up
> ```

---

### Passo 3 — Verificar se tudo subiu

Aguarde os logs mostrarem algo como:
```
api-pedidos       | Started PedidosApplication in X.XXX seconds
api-almoxarifado  | Started AlmoxarifadoApplication in X.XXX seconds
api-transporte    | Started TransporteApplication in X.XXX seconds
```

**Portas expostas:**

| Serviço | URL |
|---------|-----|
| MS Pedidos (API REST) | http://localhost:8080 |
| MS Almoxarifado | http://localhost:8081 |
| MS Transporte | http://localhost:8082 |
| RabbitMQ Management UI | http://localhost:15672 (guest/guest) |
| PostgreSQL Pedidos | localhost:5432 |
| PostgreSQL Almoxarifado | localhost:5433 |
| PostgreSQL Transporte | localhost:5434 |

---

### Passo 4 — Fazer a requisição POST (disparo do fluxo completo)

#### Via cURL (terminal):

```bash
curl -X POST http://localhost:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{"cep": "01310-100"}'
```

#### Via Postman / Insomnia:

```
Método:  POST
URL:     http://localhost:8080/api/pedidos
Headers: Content-Type: application/json
Body (raw JSON):
{
  "cep": "01310-100"
}
```

> ⚠️ **ATENÇÃO:** O CEP deve estar no formato `XXXXX-XXX` (com hífen).  
> Exemplo válido: `"01310-100"`, `"22041-001"`.  
> O microsserviço de Transporte valida o formato e rejeita CEPs sem hífen.

#### Resposta esperada (HTTP 200):

```json
{
  "mensagem": "Pedido criado e gravado com sucesso. Processamento assíncrono iniciado.",
  "pedidoId": "PED-A1B2C3D4"
}
```

---

### Passo 5 — Acompanhar o fluxo nos logs

Após o POST, observe os logs do Docker Compose em tempo real:

```
api-pedidos       | [PEDIDOS] ======= INICIANDO FLUXO DO PEDIDO: PED-XXXXXXXX =======
api-pedidos       | [PEDIDOS] Pedido salvo no banco de dados com status CRIADO.
api-pedidos       | [PEDIDOS] Evento de domínio 'pedido.criado' despachado para o mensageiro.
api-almoxarifado  | [ALMOXARIFADO] -> Evento consumido. Iniciando processamento do pedido: PED-XXXXXXXX
api-almoxarifado  | [ALMOXARIFADO] Estoque decrementado. Ordem de empacotamento salva e fechada no BD.
api-almoxarifado  | [ALMOXARIFADO] Evento 'pedido.separado' repassado ao módulo de Transporte.
api-pedidos       | [PEDIDOS] <- Evento de Feedback Recebido! Status do pedido PED-XXXXXXXX atualizado para: SEPARADO
api-transporte    | [TRANSPORTE] -> Mercadoria liberada na doca. Assumindo pedido: PED-XXXXXXXX
api-transporte    | [TRANSPORTE] Caminhão despachado (Tracking ID gerado) e salvo no BD de Logística.
api-transporte    | [TRANSPORTE] Status finalizado para o módulo de Pedidos. Ciclo concluído.
api-pedidos       | [PEDIDOS] <- Evento de Feedback Recebido! Status do pedido PED-XXXXXXXX atualizado para: EM_ROTA
```

---

### Passo 6 — Verificar o RabbitMQ Management

Acesse: **http://localhost:15672** (usuário: `guest`, senha: `guest`)

Filas criadas automaticamente:
- `petfriends.almoxarifado.queue` — Almoxarifado escuta `pedido.criado`
- `petfriends.transporte.queue` — Transporte escuta `pedido.separado`
- `petfriends.pedidos.status.queue` — Pedidos escuta `pedido.status.update`

Exchange: `petfriends.exchange` (TopicExchange)

---

### Passo 7 — Derrubar os serviços

```bash
docker compose down
```

Para remover também os volumes (dados do banco):
```bash
docker compose down -v
```

---

## 📋 Mapeamento Questões da Prova → Código

| Questão | Implementação |
|---------|---------------|
| Q1 — Entity + Repository Almoxarifado | `OrdemServicoAlmoxarifado.java` + `OrdemServicoRepository.java` |
| Q2 — Value Object Almoxarifado | `EnderecoEstoque.java` (corredor + prateleira, com equals/hashCode) |
| Q3 — Entity + Repository Transporte | `Entrega.java` + `EntregaRepository.java` |
| Q4 — Value Object Transporte | `DadosDestino.java` (cep + logradouro, com validação de formato) |
| Q9 — Config RabbitMQ Almoxarifado | `PetFriends_Almoxarifado/config/RabbitMQConfig.java` |
| Q10 — Service listener Almoxarifado | `PedidoCriadoListener.java` |
| Q11 — Config RabbitMQ Transporte | `PetFriends_Transporte/config/RabbitMQConfig.java` |
| Q12 — Service listener Transporte | `PedidoSeparadoListener.java` |
| Q5, Q6, Q7, Q8, Q13–Q16 | Respostas teóricas (ver docs/Exercicios_PetFriends.md) |

---

## 🧩 Sobre o Microsserviço de Pedidos

O `PetFriends_Pedidos` não foi exigido diretamente nas questões, mas é o **ponto de entrada do sistema** e é necessário para:
- Receber a requisição REST do cliente (PetFriends_Web)
- Gerar um `pedidoId` único e persistir o pedido
- Publicar o evento `pedido.criado` no RabbitMQ
- Atualizar o status do pedido conforme os outros serviços respondem

---

## ⚙️ Detalhes das Filas e Roteamento

```
Exchange: petfriends.exchange  (TopicExchange)
│
├── Routing Key: "pedido.criado"
│       └── → petfriends.almoxarifado.queue  (Almoxarifado consome)
│
├── Routing Key: "pedido.separado"
│       └── → petfriends.transporte.queue    (Transporte consome)
│
└── Routing Key: "pedido.status.update"
        └── → petfriends.pedidos.status.queue (Pedidos consome para sync de status)
```

---

## 📝 Observações Técnicas

- Todos os serviços usam **multi-stage Docker build** (Maven compila + JRE executa), reduzindo o tamanho da imagem final.
- O Spring Boot gerencia o `Jackson2JsonMessageConverter` automaticamente como `MessageConverter` para o `RabbitTemplate` e os `@RabbitListener`.
- O `pedidoId` (ex: `PED-A1B2C3D4`) é o **ID de Correlação** que atravessa todos os microsserviços, permitindo rastrear um mesmo pedido no Almoxarifado, Transporte e Pedidos.
- O `ddl-auto: update` cria as tabelas automaticamente na primeira execução.
