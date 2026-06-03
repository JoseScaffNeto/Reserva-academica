# Plataforma de Reserva de Ambientes e Recursos Acadêmicos

Sistema de gerenciamento de reservas de laboratórios, salas, equipamentos, projetores, notebooks e kits de aula para instituições de ensino.

## Tecnologias

- Java 17+
- Maven 3.8+ (gerenciamento de dependências)
- JUnit 5.10 + Mockito (testes unitários — integração externa exigida)

## Estrutura do Projeto

```
reserva-academica/
├── src/
│   ├── main/java/com/reserva/
│   │   ├── Main.java                  ← Classe principal (menu interativo)
│   │   ├── TestRunner.java            ← Runner manual de 40 testes
│   │   ├── model/
│   │   │   ├── Reservavel.java        ← Interface (POO)
│   │   │   ├── RecursoAcademico.java  ← Classe abstrata com herança
│   │   │   ├── Sala.java              ← Subclasse (polimorfismo)
│   │   │   ├── Equipamento.java       ← Subclasse (polimorfismo)
│   │   │   ├── Usuario.java
│   │   │   ├── Reserva.java
│   │   │   ├── TipoUsuario.java
│   │   │   ├── TipoRecurso.java
│   │   │   └── StatusReserva.java
│   │   ├── repository/                ← Repositórios em memória
│   │   ├── service/                   ← Lógica de negócio
│   │   ├── exception/                 ← Exceções customizadas
│   │   ├── menu/                      ← Submenus interativos
│   │   └── util/                      ← Helpers e dados iniciais
│   └── test/java/com/reserva/
│       ├── service/ReservaServiceTest.java
│       ├── service/UsuarioServiceTest.java
│       ├── service/RecursoServiceTest.java
│       ├── service/RelatorioServiceTest.java
│       └── model/ReservaConflitoPeriodoTest.java
└── pom.xml
```

## Conceitos de POO Aplicados

| Conceito        | Onde está aplicado |
|----------------|---------------------|
| **Herança**     | `RecursoAcademico` ← `Sala`, `Equipamento` |
| **Polimorfismo**| `calcularPenalidade()` — comportamento diferente em Sala e Equipamento |
| **Interface**   | `Reservavel` implementada por `RecursoAcademico` |
| **Encapsulamento** | Todos os atributos privados com getters/setters |
| **Composição**  | `Reserva` contém `RecursoAcademico` e `Usuario` |
| **Exceções**    | Hierarquia: `ReservaException` → `ConflitoPeriodoException`, `LimiteReservasException`, etc. |

## Requisitos do Sistema Implementados

### 1. Cadastro de Usuários, Ambientes e Recursos
- Até 10 reservas ativas simultâneas por usuário
- Tipos: PROFESSOR, TÉCNICO, COORDENADOR, ALUNO_AUTORIZADO
- Recursos com código, tipo, capacidade, localização, restrições

### 2. Reservas e Controle de Disponibilidade
- Data, horário início/fim, responsável, finalidade, status
- Impedimento automático de conflitos de horário
- Reservas prioritárias: aprovação automática (coordenador) ou validação manual

### 3. Devolução, Penalidades e Regras de Uso
- Registro de devolução com status: concluída, com atraso, com avaria
- Bloqueio de 7 dias por atraso, 30 dias por avaria em equipamento frágil
- Regras diferentes por tipo de recurso (polimorfismo)

### 4. Relatórios e Painéis Analíticos
- Indicadores gerais: taxa de ocupação, atrasos, cancelamentos
- Relatório mensal: recursos mais utilizados, horários de maior demanda

## Como Executar

### Pré-requisitos
- Java 17 ou superior instalado
- Maven 3.8+ (para testes JUnit)

### Opção 1 — JAR pré-compilado (mais rápido)
```bash
# Menu interativo
java -jar reserva-academica.jar

### Opção 2 — Compilar com Maven
```bash
# Compilar
mvn compile

# Rodar testes JUnit 5
mvn test

# Empacotar
mvn package

# Executar
java -jar target/reserva-academica.jar
```

### Opção 3 — Compilar manualmente com javac
```bash
# Compilar
find src/main/java -name "*.java" > sources.txt
mkdir -p target/classes
javac --release 17 -encoding UTF-8 -d target/classes @sources.txt

# Executar menu principal
java -cp target/classes com.reserva.Main

# Executar testes manuais
java -cp target/classes com.reserva.TestRunner
```

## Navegação no Menu

```
MENU PRINCIPAL
  1. Usuários          → cadastrar, listar, buscar por ID/tipo
  2. Recursos          → cadastrar sala/equipamento, ver disponíveis, desativar
  3. Reservas          → criar, aprovar, cancelar, devolver, listar por filtros
  4. Relatórios        → indicadores gerais, relatório mensal
  0. Sair
```

## Testes Unitários

O projeto inclui **uma** de testes:

### JUnit 5 (em `src/test/`) — integração externa exigida pelo requisito
```bash
mvn test
```
Cobre: `ReservaServiceTest`, `UsuarioServiceTest`, `RecursoServiceTest`, `RelatorioServiceTest`, `ReservaConflitoPeriodoTest`