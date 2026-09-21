package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.services.OrderService;
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
@RequestMapping(value = "/orders")
@Tag(name = "Pedidos", description = "Consulta dos pedidos realizados")
public class OrderResource {

    @Autowired
    private OrderService service;

    @Operation(summary = "Listar pedidos", description = "Retorna a lista completa de pedidos cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados com sucesso",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            [
                              {
                                "id": 1,
                                "moment": "2019-06-20T19:53:07Z",
                                "orderStatus": "PAID",
                                "client": {
                                  "id": 1,
                                  "name": "Maria Brown",
                                  "email": "maria@gmail.com",
                                  "phone": "988888888"
                                },
                                "items": [],
                                "payment": null,
                                "total": 181.0
                              }
                            ]
                            """)))
    })
    @GetMapping
    public ResponseEntity<List<Order>> findAll() {
        List<Order> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "Buscar pedido por id", description = "Retorna um pedido específico pelo identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado com sucesso",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "moment": "2019-06-20T19:53:07Z",
                              "orderStatus": "PAID",
                              "client": {"id": 1, "name": "Maria Brown", "email": "maria@gmail.com", "phone": "988888888"},
                              "items": [],
                              "payment": null,
                              "total": 181.0
                            }
                            """)))
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<Order> findById(@Parameter(description = "ID do pedido", example = "1")
                                          @PathVariable Long id) {
        Order obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

}
