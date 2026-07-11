package com.matutadesign.ecommerce_api.mapper;

import com.matutadesign.ecommerce_api.dto.RoupaRequestDto;
import com.matutadesign.ecommerce_api.entity.Roupa;
import org.springframework.stereotype.Component;

@Component
public class RoupaMapper {
    public Roupa toEntity(RoupaRequestDto dto) {
        return Roupa.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .preco(dto.preco())
                .sku(dto.sku())
                .qtEstoque(dto.qtEstoque())
                .tamanho(dto.tamanho())
                .cor(dto.cor())
                .imagens(dto.imagens())
                .build();
    }
}