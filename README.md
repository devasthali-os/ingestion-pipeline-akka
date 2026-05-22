# Ingestion Pipeline (Akka)

Distributed ingestion demo structured as a **multi-module monorepo** (Scala 3, Akka Typed client, Artery remoting). Run everything from the repository root—no `cd` into subprojects.

> **Upgrading from the old layout?** See [docs/MIGRATION.md](docs/MIGRATION.md) for a full comparison of what changed and how this version works vs the previous two-folder setup.

## Architecture

```
ingestion-pipeline (root aggregate)
├── ingestion-api      … shared protocols & JSON contracts (no Akka)
├── ingestion-common   … bootstrap, base HOCON, logging
├── ingestion-endpoint … remote ingestion service (port 5150)
└── ingestion-client   … sample producer client (ephemeral port)
```

```
┌─────────────────┐     Artery TCP      ┌──────────────────────┐
│ ingestion-client│ ◄─────────────────► │ ingestion-endpoint   │
│ (many instances)│                     │ (ingestion actor)    │
└─────────────────┘                     └──────────────────────┘
```

Layering mirrors large-scale service repos: **api** (contracts) → **common** (infra) → **deployable modules** (endpoint, client).

## What changed from the previous version

The repo used to be **two separate sbt 0.13 projects** (`ingestion-endpoint.akka/`, `ingestion-client.akka/`) on **Scala 2.12** and **Akka 2.5** with Netty TCP remoting. You had to `cd` into each folder to run.

| | Previous | Now |
|---|----------|-----|
| Run | `cd ingestion-endpoint.akka && sbt run` | `sbt runEndpoint` from root |
| Scala / Akka | 2.12.3 / 2.5.4 classic | 3.4.2 / 2.8.8 (typed client, classic remote handler) |
| Remoting URL | `akka.tcp://...` | `akka://...` (Artery) |
| Messages | String-built JSON | Circe types in `ingestion-api` |
| Config | `parseFile` on classpath URL | Layered HOCON via `ConfigFactory.load` |
| Logging | `println` | Logback + SLF4J |

Details, sequence diagrams, and code mapping: **[docs/MIGRATION.md](docs/MIGRATION.md)**.

## Prerequisites

- JDK 17+ (21 recommended)
- sbt 1.10+ (bootstrapped via `project/build.properties`)

## Build & run (from repo root)

```bash
# compile all modules
sbt compile

# terminal 1 — start endpoint
sbt runEndpoint

# terminal 2 — send a test event
sbt runClient

# override endpoint target
sbt "client/run -- --host 127.0.0.1 --port 5150"
```

Or: `make run-endpoint` / `make run-client`.

## Modules

| Module | Role |
|--------|------|
| `ingestion-api` | `TestEvent`, `EventProcessed`, actor path helpers |
| `ingestion-common` | `ActorSystemBootstrap`, shared `application-base.conf`, logback |
| `ingestion-endpoint` | `IngestionClassicActor` + `EndpointApp` guardian |
| `ingestion-client` | Typed `ClientBehavior` + `ClientApp` |

## Stack

- **Scala** 3.4.2
- **Akka** 2.8.8 (Typed client, Artery TCP remoting; latest line with Scala 3 artifacts on Maven Central)
- **Circe** for JSON payloads
- **Logback** + SLF4J (replaces `println`)

## References

- [Migration & comparison](docs/MIGRATION.md)
- [Akka remoting (Artery)](https://doc.akka.io/docs/akka/current/remoting-artery.html)
- [Akka Typed](https://doc.akka.io/docs/akka/current/typed/index.html)
