package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Category;
import com.example.demospringpedidos.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/categories")
@Tag(name = "Categorias", description = "Categorias associadas aos produtos")
public class CategoryResource {

    @Autowired
    private CategoryService service;

    @Operation(summary = "Listar categorias", description = "Retorna a lista completa de categorias cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorias encontradas com sucesso",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            [
                              {"id": 1, "name": "Electronics"},
                              {"id": 2, "name": "Books"}
                            ]
                            """)))
    })
    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        List<Category> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "Buscar categoria por id", description = "Retorna uma categoria específica pelo identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"id": 1, "name": "Electronics"}
                            """)))
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<Category> findById(@Parameter(description = "ID da categoria", example = "1")
                                             @PathVariable Long id) {
        Category obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

}
