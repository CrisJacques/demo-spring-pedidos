package com.example.demospringpedidos.repositories;

import com.example.demospringpedidos.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository fornece métodos padrão para acesso a dados.
// Os parâmetros são a entidade e o tipo da chave primária.
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNameIgnoreCase(String name);
}
