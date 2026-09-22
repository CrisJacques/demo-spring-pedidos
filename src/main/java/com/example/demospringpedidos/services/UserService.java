package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.repositories.UserRepository;
import com.example.demospringpedidos.services.exceptions.DatabaseException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
        return obj.orElseThrow(() -> new ResourceNotFoundException(id)); // O método orElseThrow() tenta fazer o get e
        // se não encontrar, lança a exceção passada por parâmetro
    }

    public User insert(User obj) {
        return repository.save(obj);
    }

    public void delete(Long id) {
        findById(id);// Vai retornar ResourceNotFoundException se não encontrar usuário no banco com o id informado
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public User update(Long id, User obj) {
        User entity = repository.getReferenceById(id);
        updateData(entity, obj);
        return repository.save(entity);
    }

    private void updateData(User entity, User obj) {
        // Nem todos os atributos do objeto serão atualizados (id e password não serão atualizados)
        entity.setName(obj.getName());
        entity.setEmail(obj.getEmail());
        entity.setPhone(obj.getPhone());
    }

}
