package com.example.Autentificacion_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Autentificacion_service.model.Autentificacion;

public interface AutentificacionRepository extends JpaRepository<Autentificacion, Long> {
}