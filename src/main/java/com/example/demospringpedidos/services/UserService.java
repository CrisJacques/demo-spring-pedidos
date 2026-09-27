package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.repositories.UserRepository;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import com.example.demospringpedidos.services.exceptions.DatabaseException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
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
        if (repository.existsByEmailIgnoreCase(obj.getEmail())) {
            throw new BusinessException("Email already exists.");
        }
        return repository.save(obj);
    }

    public void delete(Long id) {
        findById(id);// Vai retornar ResourceNotFoundException se não encontrar usuário no banco com o id informado
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("User has associated orders.");
        }
    }

    public User update(Long id, User obj) {
        try {
            User updatedEntity = updateData(id, obj);
            return repository.save(updatedEntity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    private User updateData(Long id, User obj) {
        if(repository.existsByEmailIgnoreCase(obj.getEmail())) {
            throw new BusinessException("Email already exists.");
        }
        User entity = repository.getReferenceById(id);
        // Nem todos os atributos do objeto serão atualizados (id não será atualizado)
        entity.setName(obj.getName());
        entity.setEmail(obj.getEmail());
        entity.setPhone(obj.getPhone());
        entity.setPassword(obj.getPassword());
        return entity;
    }

}
