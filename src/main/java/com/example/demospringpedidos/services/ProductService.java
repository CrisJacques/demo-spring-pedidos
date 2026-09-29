package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.repositories.ProductRepository;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import com.example.demospringpedidos.services.exceptions.DatabaseException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
// A anotação @Component permite que a classe seja registrada no Component Registration do Spring, habilitando-a para ser injetada como dependência usando o @Autowired
// Existem outras anotações que são equivalentes, mas que tem semântica, como @Service para registrar Services e @Repository para registrar Repositories
// No caso do ProductRepository, é opcional colocar a anotação @Repository, pois ele herda da interface JpaRepository que já está registrada como componente do Spring
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Product findById(Long id){
        Optional<Product> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public List<Product> findAllById(Set<Long> ids) {
        List<Product> products = repository.findAllById(ids);
        Set<Long> foundIds = new HashSet<>();
        for (Product product : products) {
            foundIds.add(product.getId());
        }

        for (Long id : ids) {
            if (!foundIds.contains(id)) {
                throw new ResourceNotFoundException(id);
            }
        }

        return products;
    }

    public Product insert(Product product) {
        if (repository.existsByNameIgnoreCase(product.getName())) {
            throw new BusinessException("Product already exists.");
        }
        return repository.save(product);
    }

    public Product update(Long id, Product newProductInfo) {
        if (repository.existsByNameIgnoreCase(newProductInfo.getName())) {
            throw new BusinessException("New product name already exists.");
        }
        return repository.save(updateData(id, newProductInfo));
    }

    private Product updateData(Long id, Product newProductInfo) {
        Product actualProduct = findById(id);
        actualProduct.setName(newProductInfo.getName());
        actualProduct.setDescription(newProductInfo.getDescription());
        actualProduct.setPrice(newProductInfo.getPrice());
        actualProduct.setImgUrl(newProductInfo.getImgUrl());
        actualProduct.getCategories().clear();
        actualProduct.getCategories().addAll(newProductInfo.getCategories());
        return actualProduct;
    }

    public void delete(Long id) {
        findById(id);// Vai retornar ResourceNotFoundException se não encontrar produto no banco com o id informado
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Product has associated orders.");
        }
    }

}
