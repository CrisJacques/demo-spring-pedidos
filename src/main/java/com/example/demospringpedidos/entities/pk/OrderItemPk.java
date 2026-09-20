package com.example.demospringpedidos.entities.pk;

import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.Product;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Objects;

// Classe para a chave primária composta da associação Product - Order. Esta classe não terá construtor declarado explicitamente
// (ou seja, vai usar o construtor padrão, que é o sem argumentos)
@Embeddable // Annotation usada para classes de chave primária composta. Essa anotação diz ao JPA: "Essa classe pode ser incorporada dentro de uma entidade."
public class OrderItemPk implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // Neste caso, o hashCode() e equals() devem ser gerados levando em conta os 2 atributos, pois são eles em conjunto que definem de forma única
    // a associação (diferente de outras classes já implementadas, em que usar apenas o id no hashCode e equals era suficiente para comparar de forma
    // satisfatória um objeto com os demais)
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemPk that = (OrderItemPk) o;
        return Objects.equals(order, that.order) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, product);
    }
}
