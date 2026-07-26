package com.matutadesign.ecommerce_api.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.matutadesign.ecommerce_api.entity.Banner;
import com.matutadesign.ecommerce_api.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/banner")
@CrossOrigin(origins = "*")
public class BannerController {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping
    public ResponseEntity<Banner> obterBanner() {
        List<Banner> banners = bannerRepository.findAll();
        if (banners.isEmpty()) {
            Banner padrao = new Banner(
                    "MATUTA",
                    "PEÇAS EXCLUSIVAS",
                    "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?q=80&w=800",
                    "https://images.unsplash.com/photo-1469334031218-e382a71b716b?q=80&w=800"
            );
            return ResponseEntity.ok(padrao);
        }
        return ResponseEntity.ok(banners.get(0));
    }

    @PostMapping
    public ResponseEntity<Banner> atualizarBanner(
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "subtitulo", required = false) String subtitulo,
            @RequestParam(value = "fotoEsquerda", required = false) MultipartFile fotoEsquerda,
            @RequestParam(value = "fotoDireita", required = false) MultipartFile fotoDireita
    ) {
        List<Banner> banners = bannerRepository.findAll();
        Banner banner = banners.isEmpty() ? new Banner() : banners.get(0);

        if (titulo != null && !titulo.isBlank()) banner.setTitulo(titulo);
        if (subtitulo != null && !subtitulo.isBlank()) banner.setSubtitulo(subtitulo);

        try {
            if (fotoEsquerda != null && !fotoEsquerda.isEmpty()) {
                Map res = cloudinary.uploader().upload(fotoEsquerda.getBytes(), ObjectUtils.asMap("folder", "matuta-banner"));
                banner.setFotoEsquerdaUrl((String) res.get("secure_url"));
            }
            if (fotoDireita != null && !fotoDireita.isEmpty()) {
                Map res = cloudinary.uploader().upload(fotoDireita.getBytes(), ObjectUtils.asMap("folder", "matuta-banner"));
                banner.setFotoDireitaUrl((String) res.get("secure_url"));
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        Banner salvo = bannerRepository.save(banner);
        return ResponseEntity.ok(salvo);
    }
}