package com.example.demospringpedidos.entities.pk;

import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.Product;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

// Chave primária composta da associação entre Product e Order.
// Sem construtor explícito, usa o construtor padrão sem argumentos.
// Indica ao JPA que esta classe pode ser incorporada a uma entidade.
@Embeddable
public class OrderItemPk implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @Getter
    @Setter
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @Getter
    @Setter
    private Product product;

    // Os dois atributos identificam unicamente a associação e devem participar da igualdade.
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderItemPk that = (OrderItemPk) o;
        return Objects.equals(order, that.order) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, product);
    }
}
