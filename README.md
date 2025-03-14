# Simulador de Fluxo de Empréstimo

Esta é uma aplicação que simula fluxos de empréstimo. Foi construída com o intuito de trabalhar com valores financeiros e exercitar conceitos de desenvolvimento de software como:

- Entendimento do negócio
- Boas práticas de codificação e arquitetura
  - Separação de responsabilidades
  - Boa cobertura de testes unitários e de integração
  - Documentação em código e documentação viva
  - Injeção de dependência
  - Design de APIs
  - Operações financeiras complexas
  - Performance e baixa latência
  - Internacionalização de mensagens

---

## 🚀 Setup

Para executar a aplicação, utilize a classe principal:

```shell
kotlin/com/finance/loan/simulator/LoanFlowSimulatorApp.kt
```

A aplicação responderá na porta `8080`. Um portal listando os endpoints disponíveis e suas funcionalidades estará acessível em:

🔗 **[Swagger UI](http://localhost:8080/swagger-ui/index.html)**

### Alternativamente, via Docker:

1. **Build do projeto:**
   ```shell
   ./gradlew build
   ```
2. **Geração da imagem:**
   ```shell
   docker build -t loan-flow-simulator .
   ```
3. **Execução do container:**
   ```shell
   docker run -p 8080:8080 loan-flow-simulator
   ```

📌 **Requisito:** Java instalado e variável de ambiente `JAVA_HOME` configurada.

---

## 📌 Principais Endpoints e Funcionalidades

### 📍 Simulação de Empréstimo com Taxa Simples

Esta é a funcionalidade principal do sistema. A aplicação calcula o valor das parcelas fixas utilizando a fórmula:

\[ PMT = PV \times \frac{r}{1 - (1+r)^{-n}} \]

Onde:

- **PMT** = Pagamento mensal
- **PV** = Valor presente (empréstimo)
- **r** = Taxa de juros mensal (taxa anual / 12)
- **n** = Número total de pagamentos (meses)

A taxa de juros anual é definida com base na idade informada:

| Faixa Etária      | Taxa Anual |
|-------------------|------------|
| Até 25 anos      | 5%          |
| De 26 a 40 anos  | 3%          |
| De 41 a 60 anos  | 2%          |
| Acima de 60 anos | 4%          |

**Endpoint:**
```http
POST http://localhost:8080/api/loans/simulate
```

**Exemplo de Body:**
```json
{
  "loanValue": 10000,
  "birthDate": "2010-01-19",
  "loanDurationMonths": 2
}
```

**Exemplo de Response:**
```json
{
  "loanSimulation": {
    "monthlyPayment": 5031.27,
    "finalValue": 10062.54,
    "totalInterest": 62.54,
    "yearlyRate": 0.05,
    "originalValue": 10000,
    "loanDurationMonths": 2,
    "currency": "BRL"
  }
}
```

### 📍 Simulação de Empréstimo em Batch

Esse endpoint permite calcular um grande número de simulações em paralelo. Testado com **10 mil simulações em menos de 2 segundos**.

**Endpoint:**
```http
POST http://localhost:8080/api/loans/simulate-batch
```

**Exemplo de Request:**
```json
[
  {
    "loanValue": 14000,
    "birthDate": "2011-01-19",
    "loanDurationMonths": 90
  },
  {
    "loanValue": 140000,
    "birthDate": "2001-01-19",
    "loanDurationMonths": 900
  }
]
```

---

## 🏗 Arquitetura

Este projeto segue uma **arquitetura modular e orientada a responsabilidades**, inspirada nos princípios de **arquitetura hexagonal (Ports & Adapters)**.

📂 **Estrutura de Pacotes**:

- **`actor`** – Contém a lógica de negócio central
- **`config`** – Configurações técnicas do sistema
- **`controller`** – Implementação dos endpoints da API REST
- **`model`** – Classes de domínio e DTOs
- **`queue`** – Comunicação assíncrona mockada
- **`service`** – Orquestração dos actors
- **`validator`** – Validação de entrada de dados

### 🛠 Conceitos aplicados

- **Execução paralela** – Utiliza **Kotlin Coroutines** para performance otimizada
- **Testes de stress** – Suporta **10 mil requisições simultâneas**
- **Internacionalização** – Mensagens de erro configuradas no `messages.properties`

### 📌 Principais Bibliotecas Utilizadas

- **Spring Boot** – Injeção de dependências e API REST
- **Swagger** – Documentação viva
- **Ktlint** – Padronização de código Kotlin
- **JUnit & Mockito-Kotlin** – Testes unitários e de integração
- **Jackson** – Serialização JSON
- **Kotlin Coroutines** – Execução assíncrona eficiente
- **BigDecimal** – Cálculos financeiros precisos

---

## 📜 Licença

Este projeto é de uso livre e pode ser utilizado e modificado conforme necessidade. 🚀

---
