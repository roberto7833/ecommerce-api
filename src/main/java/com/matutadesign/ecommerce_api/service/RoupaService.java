package com.matutadesign.ecommerce_api.service;

import com.matutadesign.ecommerce_api.dto.RoupaRequestDto;
import com.matutadesign.ecommerce_api.dto.RoupaResponseDto;
import com.matutadesign.ecommerce_api.entity.Roupa;
import com.matutadesign.ecommerce_api.mapper.RoupaMapper;
import com.matutadesign.ecommerce_api.repository.RoupaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoupaService {
    private final RoupaRepository roupaRepository;
    private final RoupaMapper roupaMapper;

    public RoupaService(RoupaRepository roupaRepository, RoupaMapper roupaMapper){
        this.roupaRepository = roupaRepository;
        this.roupaMapper = roupaMapper;
    }

    @Transactional
    public List<RoupaResponseDto> create(RoupaRequestDto roupaDto, MultipartFile foto){
        if (roupaRepository.existsBySku(roupaDto.sku())) {
            throw new RuntimeException("Já existe um produto com o SKU: " + roupaDto.sku());
        }

        String urlFotoFinal = "/midias/padrao.jpg";

        if (foto != null && !foto.isEmpty()) {
            try {
                String diretorioDestino = "C:/Users/rober/IdeaProjects/midias/imagens-boutique/";
                File pasta = new File(diretorioDestino);
                if (!pasta.exists()) {
                    pasta.mkdirs();
                }

                String nomeArquivo = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
                File arquivoDestino = new File(diretorioDestino + nomeArquivo);
                foto.transferTo(arquivoDestino);

                urlFotoFinal = "/midias/" + nomeArquivo;
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar a imagem no servidor local: " + e.getMessage());
            }
        }

        Roupa roupa = roupaMapper.toEntity(roupaDto);
        roupa.setImagens(List.of(urlFotoFinal));
        roupaRepository.save(roupa);
        return findAllAsDto();
    }

    @Transactional(readOnly = true)
    public Page<RoupaResponseDto> list(Pageable pageable){
        return roupaRepository.findAll(pageable).map(RoupaResponseDto::new);
    }

    @Transactional
    public List<RoupaResponseDto> update(Long id, RoupaRequestDto roupaDto){
        Roupa roupaExistente = roupaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Roupa não encontrada para atualizar."));

        roupaExistente.setNome(roupaDto.nome());
        roupaExistente.setDescricao(roupaDto.descricao());
        roupaExistente.setPreco(roupaDto.preco());
        roupaExistente.setSku(roupaDto.sku());
        roupaExistente.setQtEstoque(roupaDto.qtEstoque());
        roupaExistente.setTamanho(roupaDto.tamanho());
        roupaExistente.setCategoria(roupaDto.categoria());
        roupaExistente.setCor(roupaDto.cor());

        roupaRepository.save(roupaExistente);
        return findAllAsDto();
    }

    @Transactional
    public List<RoupaResponseDto> delete(Long id){
        if (!roupaRepository.existsById(id)) {
            throw new RuntimeException("Roupa não encontrada para remover.");
        }
        roupaRepository.deleteById(id);
        return findAllAsDto();
    }

    private List<RoupaResponseDto> findAllAsDto() {
        return roupaRepository.findAll().stream()
                .map(RoupaResponseDto::new)
                .collect(Collectors.toList());
    }
}