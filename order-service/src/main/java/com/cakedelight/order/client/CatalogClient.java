package com.cakedelight.order.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CatalogClient {

    private final RestClient restClient;

    public CatalogClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8081")
                .build();
    }

    public String getCake(Long cakeId) {
        return restClient
                .get()
                .uri("/api/cakes/{id}", cakeId)
                .retrieve()
                .body(String.class);
    }
}