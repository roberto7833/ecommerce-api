package com.matutadesign.ecommerce_api.repository;

import com.matutadesign.ecommerce_api.entity.Roupa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoupaRepository extends JpaRepository<Roupa, Long> {
    Optional<Roupa> findBySku(String sku);

    boolean existsBySku(String sku);
}
