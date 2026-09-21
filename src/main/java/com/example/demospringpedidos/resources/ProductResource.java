package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.services.ProductService;
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

}
