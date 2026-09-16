package com.example.demospringpedidos.repositories;

import com.example.demospringpedidos.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

// Neste caso, apenas criar uma interface que extends JpaRepository já será suficiente para implementar o acesso a dados, pois a
//JpaRepository é uma interface que tem uma série de métodos padrão para acesso a dados
// O primeiro argumento é a classe de entidade que queremos implementar o acesso a dados e o segundo argumento é o tipo da chave primária
public interface UserRepository extends JpaRepository<User, Long> {

}
