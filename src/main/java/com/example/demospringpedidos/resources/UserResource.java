package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.resources.exceptions.StandardError;
import com.example.demospringpedidos.services.UserService;
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
@RequestMapping(value = "/users")
@Tag(name = "Usuários", description = "Operações de cadastro e consulta de usuários")
public class UserResource {

    @Autowired
    private UserService service;

    @Operation(summary = "Listar usuários", description = "Retorna a lista completa de usuários cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários encontrados com sucesso",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            [
                              {
                                "id": 1,
                                "name": "Maria Brown",
                                "email": "maria@gmail.com",
                                "phone": "988888888"
                              }
                            ]
                            """)))
    })
    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        List<User> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "Buscar usuário por id", description = "Retorna um usuário específico pelo identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "name": "Maria Brown",
                              "email": "maria@gmail.com",
                              "phone": "988888888"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<User> findById(@Parameter(description = "ID do usuário", example = "1")
                                         @PathVariable Long id) {
        User obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @Operation(summary = "Cadastrar usuário", description = "Cria um novo usuário e retorna o recurso criado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = User.class)))
    })
    @PostMapping
    public ResponseEntity<User> insert(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, description = "Dados do novo usuário",
            content = @Content(examples = @ExampleObject(value = """
                    {
                      "name": "João da Silva",
                      "email": "joao@example.com",
                      "phone": "999999999",
                      "password": "123456"
                    }
                    """))) @RequestBody User obj) {
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).body(obj); // Retornando status code 201 com a uri do recurso criado no
        // header Location e o objeto inserido no body
    }

    @Operation(summary = "Excluir usuário", description = "Remove um usuário pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "400", description = "Erro de banco de dados",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID do usuário", example = "1")
                                       @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<User> update(@Parameter(description = "ID do usuário", example = "1")
                                       @PathVariable Long id,
                                       @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                               required = true, description = "Dados atualizados do usuário",
                                               content = @Content(examples = @ExampleObject(value = """
                                                       {
                                                         "name": "Maria Brown",
                                                         "email": "maria.brown@example.com",
                                                         "phone": "988888888"
                                                       }
                                                       """))) @RequestBody User obj) {
        obj = service.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }

}
