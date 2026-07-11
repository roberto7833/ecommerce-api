package com.matutadesign.ecommerce_api.dto;

import com.matutadesign.ecommerce_api.entity.Tamanho;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record RoupaRequestDto(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,
        @Size(max = 250, message = "A descrição deve ter no máximo 250 caracteres")
        String descricao,
        double preco,
        String sku,
        Integer qtEstoque,
        @NotNull(message = "O tamanho é obrigatório")
        Tamanho tamanho,
        @NotBlank(message = "A cor é obrigatória")
        String cor,
        List<String> imagens
) {}