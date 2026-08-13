\# Cake Delight - API Documentation



\## 1. Overview



Cake Delight exposes REST APIs through the API Gateway.



The API Gateway runs on port `8080` and routes requests to the individual microservices.



```text

Frontend

&#x20;  |

&#x20;  v

API Gateway :8080

&#x20;  |

&#x20;  +---- /api/cakes ------> Catalog Service :8081

&#x20;  |

&#x20;  +---- /api/orders -----> Order Service :8082

&#x20;  |

&#x20;  +---- /api/ratings ----> Rating Service :8083

