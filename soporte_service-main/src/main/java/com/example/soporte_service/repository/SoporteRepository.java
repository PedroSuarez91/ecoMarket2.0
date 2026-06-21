package com.example.soporte_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.soporte_service.model.Soporte;

public interface SoporteRepository extends JpaRepository<Soporte, Long> {

}