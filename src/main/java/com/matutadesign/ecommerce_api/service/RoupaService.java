package com.matutadesign.ecommerce_api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.matutadesign.ecommerce_api.dto.RoupaRequestDto;
import com.matutadesign.ecommerce_api.entity.Roupa;
import com.matutadesign.ecommerce_api.repository.RoupaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RoupaService {

    @Autowired
    private RoupaRepository roupaRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Transactional(readOnly = true)
    public List<Roupa> listarTodas() {
        return roupaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Roupa buscarPorId(Long id) {
        return roupaRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean existeSku(String sku) {
        return roupaRepository.existsBySku(sku);
    }

    @Transactional
    public Roupa salvar(RoupaRequestDto roupaDTO, List<MultipartFile> fotos) {
        Roupa roupa = new Roupa();
        roupa.setNome(roupaDTO.nome());
        roupa.setDescricao(roupaDTO.descricao());
        roupa.setPreco(roupaDTO.preco());
        roupa.setSku(roupaDTO.sku());
        roupa.setQtEstoque(roupaDTO.qtEstoque());
        roupa.setTamanho(roupaDTO.tamanho());
        roupa.setCategoria(roupaDTO.categoria());
        roupa.setCor(roupaDTO.cor());

        List<String> urlsFotos = enviarFotosParaCloudinary(fotos);
        if (urlsFotos.isEmpty()) {
            urlsFotos.add("https://images.unsplash.com/photo-1434389677669-e08b4cac3105?q=80&w=500");
        }
        roupa.setImagens(urlsFotos);

        return roupaRepository.save(roupa);
    }

    @Transactional
    public Roupa atualizar(Long id, RoupaRequestDto roupaDTO, List<MultipartFile> fotos) {
        Roupa roupaExistente = roupaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Roupa não encontrada para atualizar."));

        roupaExistente.setNome(roupaDTO.nome());
        roupaExistente.setDescricao(roupaDTO.descricao());
        roupaExistente.setPreco(roupaDTO.preco());
        roupaExistente.setQtEstoque(roupaDTO.qtEstoque());
        roupaExistente.setTamanho(roupaDTO.tamanho());
        roupaExistente.setCategoria(roupaDTO.categoria());
        roupaExistente.setCor(roupaDTO.cor());

        List<String> imagensFinais = new ArrayList<>();
        if (roupaDTO.imagens() != null && !roupaDTO.imagens().isEmpty()) {
            imagensFinais.addAll(roupaDTO.imagens());
        } else {
            imagensFinais.addAll(roupaExistente.getImagens());
        }

        if (fotos != null && !fotos.isEmpty() && !fotos.get(0).isEmpty()) {
            List<String> novasUrls = enviarFotosParaCloudinary(fotos);
            imagensFinais.addAll(novasUrls);
        }

        if (imagensFinais.isEmpty()) {
            imagensFinais.add("https://images.unsplash.com/photo-1434389677669-e08b4cac3105?q=80&w=500");
        }

        roupaExistente.getImagens().clear();
        roupaExistente.getImagens().addAll(imagensFinais);

        return roupaRepository.save(roupaExistente);
    }

    @Transactional
    public void deletar(Long id) {
        roupaRepository.deleteById(id);
    }

    // Faz o upload direto para o Cloudinary e gera URLs públicas permanentes
    private List<String> enviarFotosParaCloudinary(List<MultipartFile> fotos) {
        List<String> urlsPublicas = new ArrayList<>();

        if (fotos != null && !fotos.isEmpty()) {
            for (MultipartFile foto : fotos) {
                if (foto != null && !foto.isEmpty()) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(foto.getBytes(),
                                ObjectUtils.asMap("folder", "matuta-boutique"));

                        String urlSegura = (String) uploadResult.get("secure_url");
                        urlsPublicas.add(urlSegura);
                    } catch (IOException e) {
                        throw new RuntimeException("Erro ao enviar foto para o Cloudinary: " + e.getMessage());
                    }
                }
            }
        }
        return urlsPublicas;
    }
}