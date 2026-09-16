package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
// A anotação @Component permite que a classe seja registrada no Component Registration do Spring, habilitando-a para ser injetada como dependência usando o @Autowired
// Existem outras anotações que são equivalentes, mas que tem semântica, como @Service para registrar Services e @Repository para registrar Repositories
// No caso do UserRepository, é opcional colocar a anotação @Repository, pois ele herda da interface JpaRepository que já está registrada como componente do Spring
public class UserService {

    @Autowired
    private UserRepository repository;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id){
        Optional<User> obj = repository.findById(id);
        return obj.get();
    }

}
