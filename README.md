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

🔗 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

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

### 📍 Parâmetros alternativos no Body
**email**: Se você informar, o sistema simula utilização de fila (mockada em memória) para demonstrar envio assíncrono
**inputCurrency** e **outputCurrency**: Informe **BRL**, **USD**,  **EUR**, **JPY** ou **CNY** para obter uma conversão de moeda na entrada do dado se informar inputCurreny, e na exibição da resposta ao informar outputCurrency
O default para ambos os campos é BRL

Exemplo de Request com todos os campos não obrigatórios:
```json
{
  "loanValue":10000,
  "birthDate": "2010-01-19",
  "loanDurationMonths": 2,
  "email": "marciocanovas@gmail.com",
  "inputCurrency": "BRL",
  "outputCurrency": "BRL"
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

**Exemplo de Response:**
```json
[
  {
    "loanSimulation": {
      "monthlyPayment": 186.86,
      "finalValue": 16817.4,
      "totalInterest": 2817.4,
      "yearlyRate": 0.05,
      "originalValue": 14000,
      "loanDurationMonths": 90,
      "currency": "BRL"
    }
  },
  {
    "loanSimulation": {
      "monthlyPayment": 597.49,
      "finalValue": 537741,
      "totalInterest": 397741,
      "yearlyRate": 0.05,
      "originalValue": 140000,
      "loanDurationMonths": 900,
      "currency": "BRL"
    }
  },
  {
    "loanSimulation": {
      "monthlyPayment": 247868.48,
      "finalValue": 1239342.4,
      "totalInterest": 15342.4,
      "yearlyRate": 0.05,
      "originalValue": 1224000,
      "loanDurationMonths": 5,
      "currency": "BRL"
    }
  }
]
```
### 📍 Simulação de Empréstimo com Renda Variável

Simula um empréstimo que possui uma taxa fixa e outra variável associada a um índice
Exemplo: Financiamento de imóvel com taxa fixa de 4% ao ano + variação do IPCA
Apresenta a evolução estimada do valor da parcela ao longo do tempo, utilizando números aproximados para variação do índice escolhido
Índices simulados: CDI, TR e IPCA

**Endpoint:**
```http
POST http://localhost:8080/api/loans/variable-rate/simulate
```

**Exemplo de Request:**
```json
{
  "loanValue":200000,
  "birthDate": "2010-01-19",
  "loanDurationMonths": 120,
  "fixPartRate": 0.003333,
  "financialIndex": "IPCA"
}
```
**Exemplo de Response:**
```json
{
  "finalValue": 396119.83,
  "totalInterest": 196119.83,
  "originalValue": 200000,
  "loanDurationMonths": 120,
  "evolution": [
    {
      "index": 1,
      "monthlyPayment": 2397.02,
      "fixPartRate": 0.003333,
      "variablePartRate": 0.0031
    },
    {
      "index": 2,
      "monthlyPayment": 2405.78,
      "fixPartRate": 0.003333,
      "variablePartRate": 0.00310003
    },
…
}
```

---

## 🏗 Arquitetura

Este projeto segue uma **arquitetura modular e orientada a responsabilidades**, inspirada nos princípios de **arquitetura hexagonal (Ports & Adapters)**.

📂 **Estrutura de Pacotes**:

- **`actor`** – Contém a lógica de negócio central. Os "actors" são responsáveis por processar cálculos e regras diretamente relacionadas à simulação de empréstimos. Esse design permite que a lógica de domínio fique desacoplada da camada de entrada (APIs) e infraestrutura
- **`config`** – Inclui configurações técnicas, como clocks para controle de tempo, configurações de API REST, e ajustes gerais do framework (Spring Boot, Swagger, etc)
- **`controller`** – Implementa os endpoints da API REST. Essa camada apenas recebe as requisições HTTP, valida os dados de entrada e repassa para os serviços apropriados
- **`model`** – Define as classes de domínio e DTOs utilizados no sistema. Esse pacote contém as estruturas básicas dos objetos que interagem dentro do simulador
- **`queue`** – Responsável pela comunicação assíncrona dentro do sistema. Contém classes que mockam integração com filas, permitindo que operações pesadas sejam executadas em background.
- **`service`** – Camada intermediária que orquestra os actors. Os services atuam como ponte entre os controllers e as regras de negócio, aplicando validações, chamadas a múltiplos actors e agregação de resultados.
- **`validator`** – Contém classes especializadas em validação de entrada, garantindo que os dados estejam corretos antes de serem processados pelos serviços e actors.

### 🛠 Conceitos aplicados

- **Execução paralela** – Utilizando as Coroutines do Kotlin, foi possível realizar cálculos financeiros compatíveis com processamento paralelo e que respondem bem em cenários de alta demanda
- **Stress test** – Os endpoints de simulação passam por teste de stress com 10 mil chamadas e garantia que roda em até 15 segundos. O endpoint otimizado para batch passa pelo mesmo cenário e o teste garante performance de até 2 segundos
- **Clock** – Para operações de tempo, como cálculo de idade, a implementação padrão recupera o horário do servidor, mas é possível cravar um “relógio parado” em situações de teste
- **Internacionalização** – As mensagens de erro, no lugar de cravadas no código, são chamadas através de identificadores.

- O arquivo “messages.properties” no momento apresenta apenas a versão em português, mas seria possível deixar esse arquivo em inglês, criar um arquivo messages_pt_BR.properties para configuração local brasileira e e quantas mais fossem necessárias

### 📌 Principais Bibliotecas Utilizadas

- **Spring Boot** – Disponibilização da aplicação, injeção de dependências e API REST
- **Swagger** – Documentação viva
- **Ktlint** – Linter para padronização de código Kotlin
- **JUnit & Mockito-Kotlin** – Testes unitários e de integração
- **Jackson** – Serialização JSON
- **Kotlin Coroutines** – Execução assíncrona eficiente
- **Springboot Annotations** - recuperação de propriedades do sistema
- **BigDecimal** – Para operação com ponto flutuante e cálculos financeiros precisos

---