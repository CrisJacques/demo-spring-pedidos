package com.example.demospringpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Dados recebidos para criar ou atualizar um usuário")
public record UserRequestDto(
        @NotBlank(message = "Field is required")
        @Schema(
                description = "Nome completo do usuário",
                example = "Maria Brown",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @NotBlank(message = "Field is required")
        @Email(message = "Not valid email")
        @Schema(
                description = "E-mail do usuário em formato válido",
                example = "maria@gmail.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @NotBlank(message = "Field is required")
        @Pattern(regexp = "[0-9]+", message = "Phone must contain only numbers")
        @Schema(
                description = "Telefone para contato, apenas números",
                example = "988888888",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String phone,

        @NotBlank(message = "Field is required")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).{8,}$",
                message = "The password must be at least 8 characters long, including a number, "
                        + "an uppercase letter, and a lowercase letter."
        )
        @Schema(description = "Senha com no mínimo 8 caracteres, incluindo número e letras maiúscula e minúscula",
                example = "Abcdefg1", requiredMode = Schema.RequiredMode.REQUIRED,
                accessMode = Schema.AccessMode.WRITE_ONLY)
        String password) {
}
