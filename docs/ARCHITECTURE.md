\# Cake Delight - System Architecture



\## 1. Architecture Overview



Cake Delight is a cloud-native microservices application designed using independently deployable services.



The application separates business responsibilities into dedicated microservices.



```text

&#x20;                        +----------------------+

&#x20;                        |      Frontend        |

&#x20;                        |  HTML / CSS / JS     |

&#x20;                        +----------+-----------+

&#x20;                                   |

&#x20;                                   | REST / HTTP

&#x20;                                   v

&#x20;                        +----------------------+

&#x20;                        |     API Gateway      |

&#x20;                        |       :8080          |

&#x20;                        +----------+-----------+

&#x20;                                   |

&#x20;             +---------------------+---------------------+

&#x20;             |                     |                     |

&#x20;             v                     v                     v

&#x20;  +-------------------+  +-------------------+  +-------------------+

&#x20;  | Catalog Service   |  |  Order Service    |  | Rating Service   |

&#x20;  |      :8081        |  |      :8082        |  |      :8083        |

&#x20;  +---------+---------+  +---------+---------+  +---------+---------+

&#x20;            |                      |                      |

&#x20;            v                      v                      v

&#x20;      Catalog DB              Order DB               Rating DB

&#x20;                                   |

&#x20;                                   | Order Completed Event

&#x20;                                   v

&#x20;                            +-------------+

&#x20;                            |  RabbitMQ   |

&#x20;                            |    :5672    |

&#x20;                            +------+------+

&#x20;                                   |

&#x20;                                   v

&#x20;                        +----------------------+

&#x20;                        | Notification Service |

&#x20;                        |       :8084          |

&#x20;                        +----------+-----------+

&#x20;                                   |

&#x20;                                   v

&#x20;                           Notification DB

&#x20;                                   |

&#x20;                                   v

&#x20;                             Email Service

