package com.example.demospringpedidos.entities;

import jakarta.persistence.*;

import java.io.Serializable;
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
