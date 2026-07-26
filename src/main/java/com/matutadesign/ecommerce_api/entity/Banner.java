package com.matutadesign.ecommerce_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String subtitulo;
    private String fotoEsquerdaUrl;
    private String fotoDireitaUrl;

    public Banner() {}

    public Banner(String titulo, String subtitulo, String fotoEsquerdaUrl, String fotoDireitaUrl) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.fotoEsquerdaUrl = fotoEsquerdaUrl;
        this.fotoDireitaUrl = fotoDireitaUrl;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String subtitulo) { this.subtitulo = subtitulo; }

    public String getFotoEsquerdaUrl() { return fotoEsquerdaUrl; }
    public void setFotoEsquerdaUrl(String fotoEsquerdaUrl) { this.fotoEsquerdaUrl = fotoEsquerdaUrl; }

    public String getFotoDireitaUrl() { return fotoDireitaUrl; }
    public void setFotoDireitaUrl(String fotoDireitaUrl) { this.fotoDireitaUrl = fotoDireitaUrl; }
}