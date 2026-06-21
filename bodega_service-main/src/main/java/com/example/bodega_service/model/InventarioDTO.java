package com.example.bodega_service.model;

import lombok.Data;

@Data
public class InventarioDTO {
    private Long idInventario;
    private Integer stockDisponible;
}