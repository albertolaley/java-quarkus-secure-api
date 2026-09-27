package com.example.productapi;

import java.util.Optional;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<Product, Long> {
    public Optional<Product> findBySku(String sku) {
        return find("sku", sku).firstResultOptional();
    }
}