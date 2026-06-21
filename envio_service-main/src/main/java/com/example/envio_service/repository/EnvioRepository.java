package com.example.envio_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.envio_service.model.Envio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
}