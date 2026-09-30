package com.example.productapi;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Table(name = "products")
@Schema(name = "Product", description = "A product in the catalog")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Server-generated product ID", example = "42", readOnly = true)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    @Schema(description = "Product name", example = "Wireless keyboard", required = true)
    private String nombre;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 12, scale = 2)
    @Schema(description = "Unit price; must be zero or greater", example = "49.99", required = true)
    private BigDecimal precio;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    @Schema(description = "Available quantity; must be zero or greater", example = "25", required = true)
    private Integer stock;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Schema(description = "Unique stock-keeping unit", example = "KB-WL-001", required = true)
    private String sku;

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}