package com.example.demospringpedidos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_product")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String REQUIRED_FIELD = "Field is required";
    private static final String NOT_NULL = "Field can not be null";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do produto", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = REQUIRED_FIELD)
    @Schema(description = "Nome do produto", example = "The Lord of the Rings",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = REQUIRED_FIELD)
    @Schema(description = "Descrição detalhada do produto", example = "Lorem ipsum dolor sit amet, consectetur.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @NotNull(message = NOT_NULL)
    @Positive(message = "Price must be greater than zero")
    @Schema(description = "Preço unitário", example = "90.5", minimum = "0",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    @Schema(description = "URL da imagem do produto", example = "https://example.com/images/lord-of-the-rings.jpg")
    private String imgUrl;

    // A relação muitos-para-muitos usa uma tabela de associação, declarada em uma das entidades.
    @ManyToMany
    @JoinTable(
            name = "tb_product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    // O Set evita categorias repetidas e o HashSet começa vazio, sem exigir ordenação.
    private Set<Category> categories = new HashSet<>();

    // A referência ao produto em OrderItem está dentro do atributo id.
    @OneToMany(mappedBy = "id.product")
    private Set<OrderItem> items = new HashSet<>();

    public Product() {

    }

    public Product(Long id, String name, String description, Double price, String imgUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    // Retorna os pedidos associados sem expor a relação recursiva no JSON.
    @JsonIgnore
    public Set<Order> getOrders() {
        Set<Order> set = new HashSet<>();
        for (OrderItem orderItem : items) {
            set.add(orderItem.getOrder());
        }
        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
