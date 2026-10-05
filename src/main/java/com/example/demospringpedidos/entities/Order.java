package com.example.demospringpedidos.entities;

import com.example.demospringpedidos.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
// Order é uma palavra reservada do SQL; por isso, a tabela usa outro nome.
@Table(name = "tb_order")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do pedido", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    @Schema(description = "Instante de criação do pedido", example = "2019-06-20T19:53:07Z")
    private Instant moment;

    @Schema(description = "Status atual do pedido", example = "PAID",
            allowableValues = {"WAITING_PAYMENT", "PAID", "SHIPPED", "DELIVERED", "CANCELED"})
    // Armazenado como inteiro para persistir o código do enum no banco de dados.
    private Integer orderStatus;

    // Um cliente pode possuir vários pedidos.
    @ManyToOne(optional = false)
    // A chave estrangeira é obrigatória porque todo pedido pertence a um cliente.
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    // A referência ao pedido em OrderItem está dentro do atributo id.
    @OneToMany(mappedBy = "id.order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    // orphanRemoval remove do banco os itens que deixam de pertencer ao pedido.

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    // order é o nome do atributo que referencia a classe Order lá na classe Payment
    // mappedBy → diz quem controla o relacionamento no banco
    // Cascade propaga as operações abaixo da entidade Order para Payment:
    //    CascadeType.PERSIST,
    //    CascadeType.MERGE,
    //    CascadeType.REMOVE,
    //    CascadeType.REFRESH,
    //    CascadeType.DETACH
    // CascadeType.ALL: "Quando eu fizer algo com o Order, faça a operação correspondente no Payment também."
    private Payment payment;

    public Order() {

    }

    public Order(Long id, Instant moment, OrderStatus orderStatus, User client) {
        this.id = id;
        this.moment = moment;
        this.client = client;
        setOrderStatus(orderStatus);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public OrderStatus getOrderStatus() {
        // Converte o código interno para o enum exposto aos chamadores.
        return OrderStatus.valueOf(orderStatus);
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        if (orderStatus != null) {
            this.orderStatus = orderStatus.getCode();
        }
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Colocando get no começo do nome do método para que o resultado apareça no json de resposta
    public Double getTotal() {
        double sum = 0.0;
        for (OrderItem orderItem : items) {
            sum += orderItem.getSubTotal();
        }
        return sum;
    }
}
