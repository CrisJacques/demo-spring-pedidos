package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.Category;
import com.example.demospringpedidos.repositories.CategoryRepository;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import com.example.demospringpedidos.services.exceptions.DatabaseException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
// @Service registra esta classe como componente de serviço no Spring.
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public List<Category> findAll() {
        return repository.findAll();
    }

    public Category findById(Long id) {
        Optional<Category> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Category insert(Category obj) {
        if (repository.existsByNameIgnoreCase(obj.getName())) {
            throw new BusinessException("Category already exists");
        }
        return repository.save(obj);
    }

    public Category update(Long id, Category obj) {
        try {
            Category entity = repository.getReferenceById(id);
            return updateData(entity, obj);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    private Category updateData(Category entity, Category obj) {
        if (repository.existsByNameIgnoreCase(obj.getName())) {
            throw new BusinessException("New name for category already exists");
        }
        entity.setName(obj.getName());
        return repository.save(entity);
    }

    public void delete(Long id) {
        findById(id); // Vai retornar ResourceNotFoundException se não encontrar categoria no banco com o id informado
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Category has associated products");
        }
    }

}
