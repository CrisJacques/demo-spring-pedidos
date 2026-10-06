package com.example.demospringpedidos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
// User é uma palavra reservada do H2; por isso, a tabela usa outro nome.
@Table(name = "tb_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String REQUIRED_FIELD = "Field is required";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do usuário", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Getter
    @Setter
    private Long id;

    @NotBlank(message = REQUIRED_FIELD)
    @Schema(
            description = "Nome completo do usuário",
            example = "Maria Brown",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Getter
    @Setter
    private String name;

    @NotBlank(message = REQUIRED_FIELD)
    @Email(message = "Not valid email")
    @Schema(
            description = "E-mail do usuário (em formato válido)",
            example = "maria@gmail.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Getter
    @Setter
    private String email;

    @NotBlank(message = REQUIRED_FIELD)
    @Pattern(regexp = "[0-9]+", message = "Phone must contain only numbers")
    @Schema(
            description = "Telefone para contato (apenas números)",
            example = "988888888",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Getter
    @Setter
    private String phone;

    @NotBlank(message = REQUIRED_FIELD)
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).{8,}$",
            message = "The password must be at least 8 characters long, including a number, "
                    + "an uppercase letter, and a lowercase letter."
    )
    @Schema(
            description = "Senha do usuário. Nunca é retornada nas respostas. Deve possuir no mínimo 8 caracteres, "
                    + "incluindo um número, uma letra maiúscula e uma letra minúscula",
            example = "Abcdefg1",
            accessMode = Schema.AccessMode.WRITE_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
    @Getter
    @Setter
    private String password;

    // Evita loop infinito ao serializar a relação bidirecional entre usuários e pedidos.
    @JsonIgnore
    // Um usuário pode ter muitos pedidos; Order.client é o lado proprietário da relação.
    @OneToMany(mappedBy = "client")
    @Getter
    private List<Order> orders = new ArrayList<>();
    // Por padrão, o JPA faz lazy loading para evitar estouro de memória.
    // Os objetos associados só são carregados quando o Jackson os solicita ao JPA.
    // Isso é ativado pela configuração spring.jpa.open-in-view=true do application.properties

    public User() {

    }

    public User(Long id, String name, String email, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


}
