package com.example.demospringpedidos.entities;

import com.example.demospringpedidos.entities.pk.OrderItemPk;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    // A chave primária composta é mapeada por OrderItemPk, anotada com @Embeddable.
    @EmbeddedId
    private OrderItemPk id = new OrderItemPk();

    @Schema(description = "Quantidade do produto no pedido", example = "2", minimum = "1")
    @Getter
    @Setter
    private Integer quantity;
    @Schema(description = "Preço unitário registrado no momento da compra", example = "90.5", minimum = "0")
    @Getter
    @Setter
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
    // Evita loop infinito na serialização: Order contém itens que referenciam o próprio Order.
    // A anotação fica no getter porque o Jackson usa os métodos get.
    @JsonIgnore
    public Order getOrder() {
        return id.getOrder();
    }

    public void setOrder(Order order) {
        id.setOrder(order);
    }

    public Product getProduct() {
        return id.getProduct();
    }

    public void setProduct(Product product) {
        id.setProduct(product);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Não dá para nomear essa função simplesmente como subTotal(), porque no Java Enterprise o que vale é o get, pois o
    // padrão do Java EE é os métodos que retornam uma informação terem seu nome iniciado com get, então
    // para o subtotal aparecer no json de resposta, precisamos que o método tenha get no começo do nome
    public Double getSubTotal() {
        return price * quantity;
    }
}
