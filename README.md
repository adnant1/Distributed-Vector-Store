# 🗃️ Distributed Vector Store

A scalable, fault-tolerant **distributed vector database** for text embeddings, built with Java, Elasticsearch, and OpenAI. This system enables high-availability **vector search with replication and consistent hashing**.

---

## ⚙️ Tech Stack

- **Java 21** — Core backend implementation
- **Elasticsearch** — Fast vector storage and top-K similarity search
- **OpenAI API** — Text embedding via `text-embedding-3-small`
- **Docker & Docker Compose** — Containerized multi-node environment

---

## 🚀 Key Features

### 🧭 Coordinator Node

- Accepts all client requests
- Routes those requests to responsible nodes
- Handles replication (e.g. RF = 2)
- Aggregates search results from nodes

### 🧱 Vector Store Nodes

- Store vector embeddings in Elasticsearch
- Handle index and query requests
- Run independently and are stateless beyond data layer

### 🔁 Consistent Hashing + Replication

- Supports N virtual nodes per physical node
- Replicates vectors across `RF` distinct nodes
- Ensures high availability and scalability

### 🔍 Top-K Similarity Search

- Distributed cosine similarity search using vector embeddings
- Aggregates and ranks results across replicas

---

## 📦 API Endpoints

### `POST /coordinator/index`

Inserts a new vector into the store:

```json
{
  "text": "Meta is rolling out VR updates to its Quest headset."
}
```

### `POST /coordinator/query`

Searches for similar vectors:

```json
{
  "query": "What's the latest in consumer tech and device launches?",
  "topK": 5
}
```

---

## 🛠️ Setup & Running the System

### 1. Clone the repo

```bash
git clone https://github.com/adnant1/Distributed-Vector-Store.git
cd distributed-vector-store
```

### 2. Set your OpenAI API key

Create a `.env` file in vector-store-node/node:

```env
OPENAI_API_KEY=sk-xxxx
```

### 3. Run with Docker Compose

This spins up:

- 1 coordinator node
- 5 vector store nodes
- 5 Elasticsearch instances

```bash
docker-compose up --build
```

### 4. Test the API

#### Insert a vector

```bash
curl -X POST http://localhost:8080/coordinator/index \
  -H "Content-Type: application/json" \
  -d '{"text": "Traveling opens your mind to new cultures and ideas."}'
```

#### Search for vectors

```bash
curl -X POST http://localhost:8080/coordinator/query \
  -H "Content-Type: application/json" \
  -d '{"query": "Tell me about outdoor adventures and nature travel experiences.", "topK": 3}'
```

---

## 🏗️ Future Improvements

- Gossip-based node membership and discovery
- Write-ahead logging and recovery
- HNSW or Faiss support for approximate search
- Prometheus + Grafana for metrics

---

## 👨‍💻 Author

**Adnan T.** — [@adnant1](https://github.com/adnant1)
