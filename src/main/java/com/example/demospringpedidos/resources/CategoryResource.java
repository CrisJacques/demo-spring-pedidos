package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Category;
import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.services.CategoryService;
import com.example.demospringpedidos.resources.exceptions.StandardError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @Operation(summary = "Cadastrar categoria", description = "Cria uma nova categoria e retorna o recurso criado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "422", description = "Erro de negócio",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PostMapping
    public ResponseEntity<Category> insert(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = "Dados da nova categoria",
            content = @Content(examples = @ExampleObject(value = """
                    {
                      "name": "Electronics"
                    }
                    """))) @RequestBody Category obj) {
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).body(obj); // Retornando status code 201 com a uri do recurso criado no
        // header Location e o objeto inserido no body
    }

}
