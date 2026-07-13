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

    private final String DIRETORIO_DESTINO = "C:/Users/rober/IdeaProjects/midias/imagens-boutique/";

    // 1. LISTAR TODAS AS PEÇAS
    @Transactional(readOnly = true)
    public List<Roupa> listarTodas() {
        return roupaRepository.findAll();
    }

    // 2. BUSCAR PEÇA POR ID
    @Transactional(readOnly = true)
    public Roupa buscarPorId(Long id) {
        return roupaRepository.findById(id).orElse(null);
    }

    // 3. VERIFICAR SE SKU JÁ EXISTE
    @Transactional(readOnly = true)
    public boolean existeSku(String sku) {
        return roupaRepository.existsBySku(sku);
    }

    // 4. SALVAR NOVA PEÇA COM MÚLTIPLAS FOTOS
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
            urlsFotos.add("/midias/padrao.jpg");
        }
        roupa.setImagens(urlsFotos);

        return roupaRepository.save(roupa);
    }

    // 5. ATUALIZAR INFORMAÇÕES E SUBSTITUIR POR NOVAS FOTOS (Se enviadas)
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

        // 1. Recupera as fotos que o front-end pediu para MANTER
        List<String> imagensFinais = new ArrayList<>();
        if (roupaDTO.imagens() != null && !roupaDTO.imagens().isEmpty()) {
            imagensFinais.addAll(roupaDTO.imagens());
        } else {
            // Caso o DTO venha nulo por falha de mapeamento, preserva o que já estava no banco
            imagensFinais.addAll(roupaExistente.getImagens());
        }

        // 2. Processa as NOVAS fotos e ACRÉSCETA na lista existente
        if (fotos != null && !fotos.isEmpty() && !fotos.get(0).isEmpty()) {
            List<String> novasUrls = processarImagensLocais(fotos);
            imagensFinais.addAll(novasUrls);
        }

        // 3. Garante que nunca fique vazio
        if (imagensFinais.isEmpty()) {
            imagensFinais.add("/midias/padrao.jpg");
        }

        // 4. Limpa e redefine o estado da coleção para evitar problemas de persistência do JPA
        roupaExistente.getImagens().clear();
        roupaExistente.getImagens().addAll(imagensFinais);

        return roupaRepository.save(roupaExistente);
    }

    // 6. DELETAR PEÇA
    @Transactional
    public void deletar(Long id) {
        roupaRepository.deleteById(id);
    }

    // MÉTODO AUXILIAR PRIVADO PARA PROCESSAR MÚLTIPLOS ARQUIVOS
    private List<String> processarImagensLocais(List<MultipartFile> fotos) {
        List<String> urlsFinais = new ArrayList<>();

        if (fotos != null && !fotos.isEmpty()) {
            File pasta = new File(DIRETORIO_DESTINO);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            for (MultipartFile foto : fotos) {
                if (foto != null && !foto.isEmpty()) {
                    try {
                        String nomeArquivo = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
                        File arquivoDestino = new File(DIRETORIO_DESTINO + nomeArquivo);
                        foto.transferTo(arquivoDestino);

                        urlsFinais.add("/midias/" + nomeArquivo);
                    } catch (IOException e) {
                        throw new RuntimeException("Erro ao salvar um dos arquivos no servidor local: " + e.getMessage());
                    }
                }
            }
        }
        return urlsFinais;
    }
}