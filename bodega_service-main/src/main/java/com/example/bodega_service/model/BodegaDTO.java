package com.example.bodega_service.model;

import lombok.Data;

@Data
public class BodegaDTO {
    // bodega
    private Long idBodega;
    private String nombreBodega;
    private Integer capacidadMax;
    private boolean activa;
    // de sucursal
    private String direccionSucursal;
    // de inventario
    private Integer capacidadOcupada;
}