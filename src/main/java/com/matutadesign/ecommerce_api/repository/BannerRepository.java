package com.matutadesign.ecommerce_api.repository;

import com.matutadesign.ecommerce_api.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
}