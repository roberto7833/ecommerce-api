package com.matutadesign.ecommerce_api.dto;

import com.matutadesign.ecommerce_api.entity.Roupa;
import com.matutadesign.ecommerce_api.entity.Tamanho;
import java.time.LocalDateTime;
import java.util.List;

public record RoupaResponseDto(
        Long id,
        String nome,
        String descricao,
        double preco,
        String sku,
        Integer qtEstoque,
        Tamanho tamanho,
        String cor,
        LocalDateTime dataCadastro,
        LocalDateTime dataAtualizacao,
        List<String> imagens
) {
    public RoupaResponseDto(Roupa roupa){
        this(roupa.getId(), roupa.getNome(), roupa.getDescricao(), roupa.getPreco(), roupa.getSku(), roupa.getQtEstoque(), roupa.getTamanho(), roupa.getCor(), roupa.getDataCadastro(), roupa.getDataAtualizacao(), roupa.getImagens());
    }
}