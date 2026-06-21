package com.example.bodega_service.model;

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
@Table(name = "bodega")
public class Bodega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBodega;

    @NotBlank(message = "El nombre de la bodega es obligatorio")
    @Column(nullable = false)
    private String nombreBodega;

    @NotNull(message = "La capacidad maxima es obligatoria")
    @Positive(message = "La capacidad maxima debe ser mayor a 0")
    @Column(nullable = false)
    private Integer capacidadMax;

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = true)
    private Long idSucursal;

    @Column(nullable = true)
    private Long idInventario;
}