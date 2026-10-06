package com.example.demospringpedidos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String REQUIRED_FIELD = "Field is required";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da categoria", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Getter
    @Setter
    private Long id;

    @NotBlank(message = REQUIRED_FIELD)
    @Schema(description = "Nome da categoria", example = "Electronics", requiredMode = Schema.RequiredMode.REQUIRED)
    @Getter
    @Setter
    private String name;

    // Evita loop infinito ao serializar a relação bidirecional entre categorias e produtos.
    @JsonIgnore
    // O atributo categories em Product é o lado proprietário da relação.
    @ManyToMany(mappedBy = "categories")
    // Um Set impede produtos repetidos; o HashSet não depende de ordenação e começa vazio.
    @Getter
    private Set<Product> products = new HashSet<>();

    public Category() {

    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


}
