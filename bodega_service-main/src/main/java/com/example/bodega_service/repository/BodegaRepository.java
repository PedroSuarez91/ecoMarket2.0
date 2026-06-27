package com.example.bodega_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.bodega_service.model.Bodega;

public interface BodegaRepository extends JpaRepository<Bodega, Long> {
}