package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.resources.exceptions.StandardError;
import com.example.demospringpedidos.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/products")
@Tag(name = "Produtos", description = "Produtos disponíveis para os pedidos")
public class ProductResource {

    @Autowired
    private ProductService service;

    @Operation(summary = "Listar produtos", description = "Retorna a lista completa de produtos cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            [
                              {
                                "id": 1,
                                "name": "The Lord of the Rings",
                                "description": "Lorem ipsum dolor sit amet, consectetur.",
                                "price": 90.5,
                                "imgUrl": "https://example.com/images/lord-of-the-rings.jpg"
                              }
                            ]
                            """)))
    })
    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        List<Product> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "Buscar produto por id", description = "Retorna um produto específico pelo identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "name": "The Lord of the Rings",
                              "description": "Lorem ipsum dolor sit amet, consectetur.",
                              "price": 90.5,
                              "imgUrl": "https://example.com/images/lord-of-the-rings.jpg"
                            }
                            """)))
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> findById(@Parameter(description = "ID do produto", example = "1")
                                            @PathVariable Long id) {
        Product obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @Operation(summary = "Cadastrar produto", description = "Cria um novo produto e retorna o recurso criado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios estão faltando",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PostMapping
    public ResponseEntity<Product> insert(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = "Dados do novo produto",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Product.class),
                    examples = @ExampleObject(value = """
                            {
                              "name": "Notebook Gamer",
                              "description": "Notebook para jogos e trabalho.",
                              "price": 5550.0,
                              "imgUrl": "",
                              "categories": [
                                {
                                  "id": 3
                                }
                              ]
                            }
                    """))) @Valid @RequestBody Product obj) {
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).body(obj); // Retornando status code 201 com a uri do recurso criado no
        // header Location e o objeto inserido no body
    }

}
