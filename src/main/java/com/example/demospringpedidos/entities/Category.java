package com.example.demospringpedidos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da categoria", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "Nome da categoria", example = "Electronics")
    private String name;

    @JsonIgnore // Para evitar loop infinito na hora de o Jackson montar o json da resposta, pois uma categoria pode ter
    // vários produtos e um produto pode ter várias categorias. Nestes casos, a gente escolhe uma das entidades para colocar o @JsonIgnore.
    @ManyToMany(mappedBy = "categories") // Qual o nome do atributo na classe da outra entidade (Product) que referencia esta entidade (Category)
    private Set<Product> products = new HashSet<>();// Neste caso, é mais interessante usar Set ao invés de List porque queremos garantir
    // que a lista de produtos não vai ter valores repetidos. Usamos o HashSet porque o ordenamento não importa. Além disso, é importante
    // inicializar o Set para que ele não comece valendo null, ele deve começar valendo vazio

    public Category() {

    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public Set<Product> getProducts() {
        return products;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


}
