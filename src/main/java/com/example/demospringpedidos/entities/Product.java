package com.example.demospringpedidos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "tb_product")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imgUrl;

    // Quando a relação entre as entidades é muitos para muitos, essa relação será representada em uma nova tabela no banco. A anotação @JoinTable deve ser
    // usada em apenas uma das entidades relacionadas (aqui colocamos na classe Product, mas poderíamos ter colocado na classe Category).
    @ManyToMany
    @JoinTable(name = "tb_product_category", // Qual deve ser o nome da tabela que vai armazenar as chaves estrangeiras das entidades relacionadas
            joinColumns = @JoinColumn(name = "product_id"), // Qual deve ser o nome da coluna que vai referenciar a chave estrangeira referente a esta classe (Product)
            inverseJoinColumns = @JoinColumn(name = "category_id")) // Qual deve ser o nome da coluna que vai referenciar a chave estrangeira referente a outra classe (Category)
            // Obs.: se tivéssemos colocado a anotação @JoinTable na classe Category, o joinColumns seria "category_id" e o inverseJoinColumns seria "product_id"
    private Set<Category> categories = new HashSet<>(); // Neste caso, é mais interessante usar Set ao invés de List porque queremos garantir que a lista de categorias não vai ter
    // valores repetidos. Usamos o HashSet porque o ordenamento não importa. Além disso, é importante inicializar o Set para que ele não comece valendo null, ele deve começar valendo vazio

    @OneToMany(mappedBy = "id.product") // Tem que ser id.product porque o Product correspondente na classe OrderItem está dentro do atributo id
    private Set<OrderItem> items = new HashSet<>(); // Usando Set<> para avisar ao JPA não deve ser permitida a repetição de valores

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

    // Faz mais sentido retornar as Orders em que um Product está presente do que retornar as OrderItems em que ele está presente
    @JsonIgnore // Para evitar loop infinito nas chamadas do Jackson para montar o json de resposta, pois um produto tem orders que tem produtos e assim por diante
    public Set<Order> getOrders() {
        Set<Order> set = new HashSet<>();
        for (OrderItem orderItem : items) {
            set.add(orderItem.getOrder());
        }
        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
