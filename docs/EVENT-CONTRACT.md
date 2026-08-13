# Cake Delight - Order Completion Event Contract

## 1. Overview

Cake Delight uses event-driven communication between the Order Service and Notification Service.

After a successful checkout, the Order Service publishes an order completion event through RabbitMQ.

The Notification Service consumes this event and processes the customer notification.

```text
Order Service
     |
     | OrderCompletedEvent
     v
  RabbitMQ
     |
     | Consume
     v
Notification Service
     |
     +---- Save notification
     |
     +---- Send confirmation