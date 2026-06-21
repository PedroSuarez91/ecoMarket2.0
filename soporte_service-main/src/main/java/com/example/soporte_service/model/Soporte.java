package com.example.soporte_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "soporte")
public class Soporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSoporte;

    @NotNull(message = "El idUsuario es obligatorio")
    @Positive(message = "El idUsuario debe ser un numero valido")
    @Column(nullable = false)
    private Long idUsuario;

    @NotBlank(message = "El asunto es obligatorio")
    @Column(nullable = false)
    private String asunto;

    @NotBlank(message = "La descripcion es obligatoria")
    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private boolean estado;
}