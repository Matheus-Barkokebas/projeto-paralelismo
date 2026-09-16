# Projeto de Computação Paralela

Disciplina: Programação Paralela, Concorrente e Distribuída — Sistema da Informação, 5º período.
Professor: Rafael Nunes de Lima.

Processamento de uma matriz de números aplicando uma função matematicamente
custosa em cada elemento, comparando diferentes estratégias de paralelismo em
Java: sequencial, não estruturado (`ExecutorService`), estruturado
(`StructuredTaskScope`) e com estado compartilhado (`AtomicInteger` /
`ConcurrentLinkedQueue`).

## Divisão do grupo

| Parte | Responsável | Escopo |
|---|---|---|
| 1 | Matheus | Scaffolding (`Calculo`, `GeradorMatriz`, `ProcessadorSequencial`) + V2 — Paralelismo não estruturado (`ExecutorService`) |
| 2 | _(a definir)_ | V3 — Paralelismo estruturado (`StructuredTaskScope`) |
| 3 | _(a definir)_ | V4 — Estado compartilhado (a partir da V3) + harness de experimentos e tabela comparativa final |

Workflow: uma branch por pessoa (`feature/...`), commits próprios, merge via
Pull Request para `main`.

## Requisitos

- **JDK 25** — a V3 usa `StructuredTaskScope`, que ainda é uma *preview
  feature*. É necessário compilar e executar com `--enable-preview`.

## Estrutura

```
src/
  Main.java   // calcular(), gerarMatriz(), V1, V2 e menu interativo
```

## Como compilar e executar

```bash
javac --release 25 --enable-preview -d out src/*.java
java --enable-preview -cp out Main
```

## Status

- [x] V1 — Sequencial (baseline)
- [x] V2 — Paralelismo não estruturado
- [ ] V3 — Paralelismo estruturado
- [ ] V4 — Estado compartilhado
- [ ] Experimentos de desempenho e tabela comparativa final
