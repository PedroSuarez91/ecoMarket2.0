package com.example.inventario_service.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventario")
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInventario;

    @NotNull(message = "El idProducto es obligatorio")
    @Positive(message = "El idProducto debe ser un numero valido")
    @Column(nullable = false)
    private Long idProducto;

    @Column(nullable = true)
    private Long idBodega;    

    @Column(nullable = false)
    private String nombre;

    @NotNull(message = "El stock disponible es obligatorio")
    @PositiveOrZero(message = "El stock disponible no puede ser negativo")
    @Column(nullable = false)
    private Integer stockDisponible;

    @NotNull(message = "El stock minimo es obligatorio")
    @PositiveOrZero(message = "El stock minimo no puede ser negativo")
    @Column(nullable = false)
    private Integer stockMinimo;

    @Column(nullable = false)
    private LocalDate fechaActualizacion;    
}