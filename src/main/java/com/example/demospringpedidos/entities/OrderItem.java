package com.example.demospringpedidos.entities;

import com.example.demospringpedidos.entities.pk.OrderItemPk;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId // Anotação usada para criar chaves primárias compostas (ou seja, neste caso não devemos usar o @Id). Isso significa que a
    // chave primária desta entidade está dentro de um objeto e é composta por vários campos.
    // Importante! A classe usada com @EmbeddedId deve ser anotada com @Embeddable (neste caso, a classe OrderItemPk)
    private OrderItemPk id = new OrderItemPk(); // Como a chave primária é composta, foi preciso criar uma classe separada para ela para podermos usar aqui!
    // Sempre tem que inicializar a chave primária composta para evitar NullPointerException na hora de fazer os sets dos seus atributos (olhar as duas
    // primeiras linhas do construtor com argumentos, mais abaixo)

    private Integer quantity;
    private Double price;

    public OrderItem() {

    }

    // No construtor, precisaremos de um Order e um Product para poder configurar a chave primária composta
    public OrderItem(Order order, Product product, Integer quantity, Double price) {
        id.setOrder(order);
        id.setProduct(product);
        this.quantity = quantity;
        this.price = price;
    }

    // Não teremos um getter e setter para o id, pelo fato de ele ser uma chave primária composta. Em vez disso,
    // teremos getters e setters para cada um dos seus atributos, no caso Order e Product
    @JsonIgnore // Para evitar loop infinito de Order que chama OrderItem que chama Order e assim por diante quando o Jackson vai montar o json de resposta
    // Como o Order está dentro do id e no Java Enterprise o que vale é o get, por isso que colocamos o @JsonIgnore aqui.
    public Order getOrder(){
        return id.getOrder();
    }

    public void setOrder(Order order){
        id.setOrder(order);
    }

    public Product getProduct(){
        return id.getProduct();
    }

    public void setProduct(Product product){
        id.setProduct(product);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
