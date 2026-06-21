package com.example.Autentificacion_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "autentificacion_users")
public class Autentificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAutentificacion;

    @Column(nullable = false)
    private String emailUsuario;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime fechaLogin;

    @Column(nullable = true)
    private Long idUsuario;
}