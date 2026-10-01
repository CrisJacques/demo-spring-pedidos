package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.dto.OrderRequestDto;
import com.example.demospringpedidos.dto.OrderUpdateRequestDto;
import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.resources.exceptions.StandardError;
import com.example.demospringpedidos.services.OrderService;
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
                            """))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<Order> findById(@Parameter(description = "ID do pedido", example = "1")
                                          @PathVariable Long id) {
        Order obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @Operation(summary = "Criar pedido", description = "Cria um pedido para um cliente com produtos já cadastrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes ou inválidos",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "422", description = "Regra de negócio não atendida",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PostMapping
    public ResponseEntity<Order> insert(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = "Cliente e produtos identificados por ID, com suas quantidades",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderRequestDto.class),
                    examples = @ExampleObject(value = """
                            {
                              "clientId": 1,
                              "items": [
                                {"productId": 1, "quantity": 2},
                                {"productId": 3, "quantity": 1}
                              ]
                            }
                            """))) @Valid @RequestBody OrderRequestDto request) {
        Order createdOrder = service.insert(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdOrder);
    }

    @Operation(summary = "Atualizar pedido", description = "Atualiza os dados de um pedido existente, sobrescrevendo os valores com as informações fornecidas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "422", description = "Regra de negócio não atendida",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<Order> update(@Parameter(description = "ID do pedido", example = "2")
                                            @PathVariable Long id,
                                        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                required = true, description = "Dados atualizados do pedido",
                                                content = @Content(mediaType = "application/json",
                                                        schema = @Schema(implementation = OrderUpdateRequestDto.class),
                                                        examples = @ExampleObject(value = """
                                                                {
                                                                  "orderStatus": 2,
                                                                  "items": [
                                                                    {
                                                                      "productId": 1,
                                                                      "quantity": 3
                                                                    },
                                                                    {
                                                                      "productId": 3,
                                                                      "quantity": 4
                                                                    }
                                                                  ]
                                                                }
                                                       """))) @Valid @RequestBody OrderUpdateRequestDto request) {
        Order updatedOrder = service.update(id, request);
        return ResponseEntity.ok().body(updatedOrder);
    }

    @Operation(summary = "Excluir pedido", description = "Remove um pedido pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID do pedido", example = "1")
                                       @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
