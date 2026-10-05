package com.example.demospringpedidos.repositories;

import com.example.demospringpedidos.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository fornece métodos padrão para acesso a dados.
// Os parâmetros são a entidade e o tipo da chave primária.
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);
}
