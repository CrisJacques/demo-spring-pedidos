package com.example.demospringpedidos.repositories;

import com.example.demospringpedidos.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository fornece métodos padrão para acesso a dados.
// Os parâmetros são a entidade e o tipo da chave primária.
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);
}
