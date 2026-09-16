package com.example.demospringpedidos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_user") // Essa anotação é usada quando se quer que o nome da tabela no banco de dados seja diferente do nome da classe da entidade
// Neste caso, isso se tornou necessário porque User é uma palavra reservada do banco H2, então precisamos usar outro nome para a tabela para evitar conflito
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Desta forma, o id será AUTO INCREMENT no banco de dados (dá certo na maioria dos bancos de dados, mas pode ser necessário usar outra estratégia em alguns casos)
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String password;

    @JsonIgnore// Isso é importante para evitar loop infinito na serialização de json feita pelo Jackson, porque um usuário tem pedidos, mas o pedido tem um usuário associado (o @JsonIgnore pode ser colocado aqui ou lá na classe Order, depende de o que é mais interessante ver ou não no json de resposta da API)
    @OneToMany(mappedBy = "client") // Um usuário pode ter muitos pedidos. Como esta é a classe do usuário, usamos OneToMany aqui. No mappedBy, devemos informar o nome do atributo que vai armazenar o objeto User na classe Order
    private List<Order> orders = new ArrayList<>();
    // Por padrão, o JPA faz lazy loading para evitar estouro de memória.
    // Ou seja, só serão retornados os objetos associados se o Jackson pedir explicitamente ao JPA (como no caso de quando fazemos GET /users/1, vem os pedidos associados)
    // Isso é ativado pela configuração spring.jpa.open-in-view=true do application.properties

    public User(){

    }

    public User(Long id, String name, String email, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


}
