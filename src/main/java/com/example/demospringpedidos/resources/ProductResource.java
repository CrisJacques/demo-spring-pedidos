package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.resources.exceptions.StandardError;
import com.example.demospringpedidos.resources.swaggerexamples.ProductResourceSwaggerExamples;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
            @ApiResponse(
                    responseCode = "200",
                    description = "Produtos encontrados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = ProductResourceSwaggerExamples.GET_PRODUCTS_RESPONSE_SUCCESS
                            )))
    })
    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        List<Product> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "Buscar produto por id", description = "Retorna um produto específico pelo identificador.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = ProductResourceSwaggerExamples.GET_PRODUCT_BY_ID_RESPONSE_SUCCESS
                            ))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StandardError.class)))
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
            @ApiResponse(
                    responseCode = "400",
                    description = "Campos obrigatórios estão faltando ou possuem valores inválidos",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "422", description = "Produto já existe",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PostMapping
    public ResponseEntity<Product> insert(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Dados do novo produto",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(
                                    value = ProductResourceSwaggerExamples.POST_PRODUCTS_REQUEST
                            )))
            @Valid
            @RequestBody Product obj) {
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getId())
                .toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Campos obrigatórios estão faltando ou possuem valores inválidos",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "422", description = "Nome do produto já existe",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<Product> update(
            @Parameter(description = "ID do produto", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Dados atualizados do produto",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(
                                    value = ProductResourceSwaggerExamples.PUT_PRODUCT_REQUEST
                            )))
            @Valid
            @RequestBody Product obj) {
        obj = service.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }

    @Operation(summary = "Excluir produto", description = "Remove um produto pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "400", description = "Erro de banco de dados",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID do produto", example = "1")
                                       @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
