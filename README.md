\# 🍰 Cake Delight



A cloud-native cake ordering application built using \*\*Spring Boot Microservices, Docker, Kubernetes, MySQL, RabbitMQ, and a JavaScript frontend\*\*.



The application demonstrates an end-to-end cake ordering workflow including cake browsing, filtering, basket management, checkout, ratings, and event-driven order notifications.



\---



\## 📌 Project Overview



Cake Delight follows a microservices architecture where business responsibilities are separated into independently deployable services.



\### Microservices



| Service | Port | Responsibility |

|---|---:|---|

| API Gateway | 8080 | Entry point for frontend API requests |

| Catalog Service | 8081 | Cake catalog, details and filtering |

| Order Service | 8082 | Basket, checkout and order management |

| Rating Service | 8083 | Cake ratings and average ratings |

| Notification Service | 8084 | Order-completion notifications |

| RabbitMQ | 5672 | Event/message broker |



\---



\## 🏗️ Architecture



```text

&#x20;                   ┌─────────────────┐

&#x20;                   │    Frontend     │

&#x20;                   │ HTML/CSS/JS     │

&#x20;                   └────────┬────────┘

&#x20;                            │

&#x20;                            ▼

&#x20;                   ┌─────────────────┐

&#x20;                   │   API Gateway   │

&#x20;                   │     :8080       │

&#x20;                   └───────┬─────────┘

&#x20;                           │

&#x20;            ┌──────────────┼──────────────┐

&#x20;            ▼              ▼              ▼

&#x20;     ┌────────────┐ ┌────────────┐ ┌────────────┐

&#x20;     │  Catalog   │ │   Order    │ │  Rating    │

&#x20;     │   :8081    │ │   :8082    │ │   :8083    │

&#x20;     └─────┬──────┘ └─────┬──────┘ └─────┬──────┘

&#x20;           │              │              │

&#x20;           ▼              │              ▼

&#x20;        MySQL             │            MySQL

&#x20;                          │

&#x20;                          ▼

&#x20;                    ┌───────────┐

&#x20;                    │ RabbitMQ  │

&#x20;                    └─────┬─────┘

&#x20;                          │

&#x20;                          ▼

&#x20;                  ┌────────────────┐

&#x20;                  │ Notification   │

&#x20;                  │ Service :8084  │

&#x20;                  └───────┬────────┘

&#x20;                          │

&#x20;                          ▼

&#x20;                        Email

