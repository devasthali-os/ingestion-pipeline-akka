# Ingestion Pipeline (Akka)

Distributed ingestion demo structured as a **multi-module monorepo** (Scala 3, Akka Typed client, Artery remoting). 

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

## Prerequisites

- JDK 25
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

## References

- [Tech Stack](docs/Tech-Stack.md)
- [Akka remoting (Artery)](https://doc.akka.io/docs/akka/current/remoting-artery.html)
- [Akka Typed](https://doc.akka.io/docs/akka/current/typed/index.html)
