package com.matutadesign.ecommerce_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_roupas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Roupa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private double preco;
    private String sku;
    private Integer qtEstoque;

    @Enumerated(EnumType.STRING)
    private Tamanho tamanho;

    private String cor;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @ElementCollection
    @CollectionTable(name = "tb_roupa_imagens", joinColumns = @JoinColumn(name = "roupa_id"))
    @Column(name = "url_imagem")
    private List<String> imagens;

    @PrePersist
    protected void onCreated() {
        this.dataCadastro = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}