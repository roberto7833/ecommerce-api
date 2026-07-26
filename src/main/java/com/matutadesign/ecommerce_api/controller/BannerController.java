package com.matutadesign.ecommerce_api.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.matutadesign.ecommerce_api.entity.Banner;
import com.matutadesign.ecommerce_api.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        try {
            List<Banner> banners = bannerRepository.findAll();
            if (banners.isEmpty()) {
                Banner padrao = new Banner("MATUTA", "PEÇAS EXCLUSIVAS", null, null);
                return ResponseEntity.ok(padrao);
            }
            return ResponseEntity.ok(banners.get(0));
        } catch (Exception e) {
            Banner padrao = new Banner("MATUTA", "PEÇAS EXCLUSIVAS", null, null);
            return ResponseEntity.ok(padrao);
        }
    }

    @PostMapping
    public ResponseEntity<?> atualizarBanner(
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "subtitulo", required = false) String subtitulo,
            @RequestParam(value = "fotoEsquerda", required = false) MultipartFile fotoEsquerda,
            @RequestParam(value = "fotoDireita", required = false) MultipartFile fotoDireita
    ) {
        try {
            List<Banner> banners = bannerRepository.findAll();
            Banner banner = banners.isEmpty() ? new Banner() : banners.get(0);

            if (titulo != null && !titulo.isBlank()) {
                banner.setTitulo(titulo.trim());
            } else if (banner.getTitulo() == null) {
                banner.setTitulo("MATUTA");
            }

            if (subtitulo != null && !subtitulo.isBlank()) {
                banner.setSubtitulo(subtitulo.trim());
            } else if (banner.getSubtitulo() == null) {
                banner.setSubtitulo("PEÇAS EXCLUSIVAS");
            }

            // Upload Foto Esquerda (Cloudinary)
            if (fotoEsquerda != null && !fotoEsquerda.isEmpty() && fotoEsquerda.getSize() > 0) {
                Map res = cloudinary.uploader().upload(fotoEsquerda.getBytes(), ObjectUtils.asMap("folder", "matuta-banner"));
                if (res != null && res.containsKey("secure_url")) {
                    banner.setFotoEsquerdaUrl((String) res.get("secure_url"));
                }
            }

            // Upload Foto Direita (Cloudinary)
            if (fotoDireita != null && !fotoDireita.isEmpty() && fotoDireita.getSize() > 0) {
                Map res = cloudinary.uploader().upload(fotoDireita.getBytes(), ObjectUtils.asMap("folder", "matuta-banner"));
                if (res != null && res.containsKey("secure_url")) {
                    banner.setFotoDireitaUrl((String) res.get("secure_url"));
                }
            }

            Banner salvo = bannerRepository.save(banner);
            return ResponseEntity.ok(salvo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro detalhado: " + e.getMessage());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetarBanner() {
        try {
            List<Banner> banners = bannerRepository.findAll();
            Banner banner = banners.isEmpty() ? new Banner() : banners.get(0);

            banner.setTitulo("MATUTA");
            banner.setSubtitulo("PEÇAS EXCLUSIVAS");
            banner.setFotoEsquerdaUrl(null);
            banner.setFotoDireitaUrl(null);

            Banner salvo = bannerRepository.save(banner);
            return ResponseEntity.ok(salvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao resetar: " + e.getMessage());
        }
    }
}