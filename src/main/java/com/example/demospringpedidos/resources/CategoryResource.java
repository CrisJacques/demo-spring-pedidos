package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Category;
import com.example.demospringpedidos.services.CategoryService;
import com.example.demospringpedidos.resources.exceptions.StandardError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Category.class)),
                            examples = @ExampleObject(value = """
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
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Category.class),
                            examples = @ExampleObject(value = """
                            {"id": 1, "name": "Electronics"}
                            """))),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class)))
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
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Category.class),
                    examples = @ExampleObject(value = """
                    {
                      "name": "Electronics"
                    }
                    """))) @RequestBody Category obj) {
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).body(obj); // Retornando status code 201 com a uri do recurso criado no
        // header Location e o objeto inserido no body
    }

    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "422", description = "Nome da categoria já existente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class)))
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<Category> update(@Parameter(description = "ID da categoria", example = "1")
                                       @PathVariable Long id,
                                       @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                               required = true, description = "Dados atualizados da categoria",
                                               content = @Content(mediaType = "application/json",
                                                       schema = @Schema(implementation = Category.class),
                                                       examples = @ExampleObject(value = """
                                                       {
                                                         "name": "Smartphones"
                                                       }
                                                       """))) @RequestBody Category obj) {
        obj = service.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }

    @Operation(summary = "Excluir categoria",
            description = "Remove uma categoria pelo identificador. Em caso de sucesso, não retorna conteúdo.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria removida com sucesso",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "400", description = "Erro de banco de dados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class)))
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID da categoria", example = "1", required = true)
                                       @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
