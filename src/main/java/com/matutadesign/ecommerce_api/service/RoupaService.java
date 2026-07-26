package com.matutadesign.ecommerce_api.service;

import com.matutadesign.ecommerce_api.dto.RoupaRequestDto;
import com.matutadesign.ecommerce_api.entity.Roupa;
import com.matutadesign.ecommerce_api.repository.RoupaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoupaService {

    @Autowired
    private RoupaRepository roupaRepository;

    // Detecta dinamicamente o Sistema Operacional (Windows ou Linux/Render)
    private String obterDiretorioDestino() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "C:/Users/rober/IdeaProjects/midias/imagens-boutique/";
        } else {
            return "/tmp/imagens-boutique/";
        }
    }

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

        List<String> urlsFotos = processarImagensLocais(fotos);
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
            List<String> novasUrls = processarImagensLocais(fotos);
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

    private List<String> processarImagensLocais(List<MultipartFile> fotos) {
        List<String> urlsFinais = new ArrayList<>();
        String pastaDestino = obterDiretorioDestino();

        if (fotos != null && !fotos.isEmpty()) {
            File pasta = new File(pastaDestino);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            for (MultipartFile foto : fotos) {
                if (foto != null && !foto.isEmpty()) {
                    try {
                        String nomeArquivo = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
                        File arquivoDestino = new File(pasta, nomeArquivo);
                        foto.transferTo(arquivoDestino);

                        urlsFinais.add("/midias/" + nomeArquivo);
                    } catch (IOException e) {
                        throw new RuntimeException("Erro ao salvar um dos arquivos no servidor: " + e.getMessage());
                    }
                }
            }
        }
        return urlsFinais;
    }
}