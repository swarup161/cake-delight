\# Cake Delight - Database Schema and Data Model



\## 1. Overview



Cake Delight uses persistent storage for the business data managed by the individual microservices.



Each microservice is responsible for its own domain data.



```text

Catalog Service

&#x20;     |

&#x20;     v

&#x20; Catalog DB



Order Service

&#x20;     |

&#x20;     v

&#x20;  Order DB



Rating Service

&#x20;     |

&#x20;     v

&#x20; Rating DB



Notification Service

&#x20;     |

&#x20;     v

Notification DB

