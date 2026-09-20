package com.example.demospringpedidos.entities;

import com.example.demospringpedidos.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_order") // Essa anotação é usada quando se quer que o nome da tabela no banco de dados seja diferente do nome da classe da entidade
// Neste caso, isso se tornou necessário porque Order é uma palavra reservada do SQL, então precisamos usar outro nome para a tabela para evitar conflito
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    private Instant moment;

    private Integer orderStatus; // Mesmo o OrderStatus sendo um tipo enumerado (enum), internamente na classe trataremos ele como inteiro
    // para ficar mais claro que ele será salvo no banco de dados como um inteiro

    @ManyToOne // Um cliente pode possuir muitos pedidos. Como esta classe é a do pedido, então aqui usamos ManyToOne
    @JoinColumn(name = "client_id") // Aqui configuramos o nome da chave estrangeira que será criada no banco de dados
    private User client;

    @OneToMany(mappedBy = "id.order") // Tem que ser id.order porque o Order correspondente na classe OrderItem está dentro do atributo id
    private Set<OrderItem> items = new HashSet<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    // order é o nome do atributo que referencia a classe Order lá na classe Payment
    // mappedBy → diz quem controla o relacionamento no banco
    // cascade → diz quais operações devem ser propagadas entre as entidades - no caso acima, seriam todos os tipos de cascade:
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
        return OrderStatus.valueOf(orderStatus); // OrderStatus é guardado como inteiro dentro da classe, mas para o mundo exterior
        // ele é retornado com o valor correspondente, não o código numérico
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        if (orderStatus != null){
            this.orderStatus = orderStatus.getCode();
        }
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Set<OrderItem> getItems() {
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
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Colocando get no começo do nome do método para que o resultado apareça no json de resposta
    public Double getTotal(){
        double sum = 0.0;
        for(OrderItem orderItem : items){
            sum += orderItem.getSubTotal();
        }
        return sum;
    }
}
