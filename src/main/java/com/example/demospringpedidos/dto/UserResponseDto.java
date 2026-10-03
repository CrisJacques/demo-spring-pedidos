package com.example.demospringpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados públicos do usuário")
public record UserResponseDto(
        @Schema(description = "Identificador único do usuário", example = "1")
        Long id,

        @Schema(description = "Nome completo do usuário", example = "Maria Brown")
        String name,

        @Schema(description = "E-mail do usuário", example = "maria@gmail.com")
        String email,

        @Schema(description = "Telefone para contato", example = "988888888")
        String phone) {
}
