package com.matutadesign.ecommerce_api.controller;

import com.matutadesign.ecommerce_api.dto.RoupaRequestDto;
import com.matutadesign.ecommerce_api.dto.RoupaResponseDto;
import com.matutadesign.ecommerce_api.service.RoupaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/todos")
public class RoupaController {
    private final RoupaService roupaService;

    public RoupaController(RoupaService roupaService){
        this.roupaService = roupaService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<RoupaResponseDto> create(
            @RequestPart("data") @Valid RoupaRequestDto todoDTO,
            @RequestPart(value = "foto", required = false) MultipartFile foto){
        return roupaService.create(todoDTO, foto);
    }

    @GetMapping
    public Page<RoupaResponseDto> list(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        return roupaService.list(pageable);
    }

    @PutMapping("/{id}")
    public List<RoupaResponseDto> update(@PathVariable("id") Long id, @Valid @RequestBody RoupaRequestDto roupaDto){
        return roupaService.update(id, roupaDto);
    }

    @DeleteMapping("/{id}")
    public List<RoupaResponseDto> delete(@PathVariable("id") Long id){
        return roupaService.delete(id);
    }
}