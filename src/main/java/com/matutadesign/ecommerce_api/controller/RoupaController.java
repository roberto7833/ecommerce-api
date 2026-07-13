package com.matutadesign.ecommerce_api.controller;

import com.matutadesign.ecommerce_api.dto.RoupaRequestDto;
import com.matutadesign.ecommerce_api.entity.Roupa;
import com.matutadesign.ecommerce_api.service.RoupaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/todos")
@CrossOrigin(origins = "*")
public class RoupaController {

    @Autowired
    private RoupaService roupaService;

    // 1. LISTAR TODOS OS PRODUTOS
    @GetMapping
    public ResponseEntity<List<Roupa>> listarTodos() {
        List<Roupa> roupas = roupaService.listarTodas();
        return ResponseEntity.ok(roupas);
    }

    // 2. BUSCAR PEÇA POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Roupa> buscarPorId(@PathVariable Long id) {
        Roupa roupa = roupaService.buscarPorId(id);
        if (roupa != null) {
            return ResponseEntity.ok(roupa);
        }
        return ResponseEntity.notFound().build();
    }

    // 3. CADASTRAR NOVA PEÇA COM MÚLTIPLAS FOTOS
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> salvarRoupa(
            @RequestPart("data") RoupaRequestDto roupaDTO,
            @RequestPart(value = "foto", required = false) List<MultipartFile> fotos) {
        try {
            if (roupaService.existeSku(roupaDTO.sku())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Erro: Já existe um produto cadastrado com este SKU.");
            }

            Roupa novaRoupa = roupaService.salvar(roupaDTO, fotos);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaRoupa);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar o produto: " + e.getMessage());
        }
    }

    // 4. ATUALIZAR INFORMAÇÕES E NOVAS FOTOS (Método PUT via FormData)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> atualizarRoupa(
            @PathVariable Long id,
            @RequestPart("data") RoupaRequestDto roupaDTO,
            @RequestPart(value = "foto", required = false) List<MultipartFile> fotos) {
        try {
            Roupa roupaExistente = roupaService.buscarPorId(id);
            if (roupaExistente == null) {
                return ResponseEntity.notFound().build();
            }

            Roupa roupaAtualizada = roupaService.atualizar(id, roupaDTO, fotos);
            return ResponseEntity.ok(roupaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar o produto: " + e.getMessage());
        }
    }

    // 5. DELETAR PEÇA DO ACERVO
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarRoupa(@PathVariable Long id) {
        try {
            Roupa roupa = roupaService.buscarPorId(id);
            if (roupa == null) {
                return ResponseEntity.notFound().build();
            }

            roupaService.deletar(id);
            return ResponseEntity.ok().body("Produto removido com sucesso do catálogo.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar o produto: " + e.getMessage());
        }
    }
}