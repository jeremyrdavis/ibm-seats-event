# Demo Flow: Thoughts App (Quarkus Showcase)

## Overview
This demo builds a "Thoughts App" to showcase the developer inner loop, data persistence, messaging, AI integration, and Kubernetes deployment using Quarkus.

### Part 1: Foundations & CRUD
| Step | Action | Component | Key Command/Feature |
|------|------------|-----------------|-------------------------------------------------------------------------|
| 1 | Bootstrap | code.quarkus.io | Select: Jackson, Panache, PostgreSQL, Qute, Kafka, LangChain4j, Kubernetes. |
| 2 | Inner Loop | CLI/Maven | Run quarkus dev or mvn quarkus:dev to show live reload. |
| 3 | Data Model | Panache Entity | Create Thought.java extending PanacheEntity. |
| 4 | Seed Data | import.sql | Paste SQL to auto-populate the DB via Dev Services. |
| 5 | REST API | JAX-RS | Implement CRUD resource using Thought.listAll() and persist(). |
| 6 | UI | Qute | Create a .html template to display a random thought. |
| 7 | Demo | End-to-End | Add a thought via UI/REST and show immediate update. |

### Part 2: Event-Driven AI
| Step | Action | Component | Key Action |
|------|-----------|-----------|---------------------------------------------------------------------------|
| 8 | Messaging | Kafka Out | Add @Channel("thoughts") Emitter<Thought> to the REST resource. |
| 9 | Publish | Producer | Send every new thought to a Kafka topic. |
| 10 | App 2 | Consumer | Generate a 2nd app with @Incoming("thoughts") to listen for events. |
| 11 | AI Agent | LangChain4j | Create @RegisterAiService to analyze sentiment or generate tags using an LLM. |

### Part 3: Deployment
| Step | Action | Component | Key Action |
|------|--------|-----------|-----------------------------------------------------------|
| 12 | K8s | Extension | Run ./mvnw package to generate Kubernetes/OpenShift manifests. |
| 13 | Deploy | Orchestration | kubectl apply or oc apply to deploy both services and Kafka. |



## Sources:
[quarkus-dev] Agentic Todo example: where to put it?
Camel Quarkus Demo
Quarkus Demo Script
Quarkus Intro Demo
Demo Script
https://developers.redhat.com/articles/2022/08/08/opentelemetry-quarkus-superheroes-demo-observability
https://quarkus.io/quarkus-workshops/super-heroes/spine.html
https://github.com/holly-cummins/simple-quarkus-demo
https://kumarpallav.dev/quarkus-demo-application
https://quarkus.io/blog/quickjs4j/

