package ecomarket.sucursal_ms.model;

import lombok.Data;

@Data
public class BodegaDTO {
    private Long idBodega;
    private String nombreBodega;
    private Integer capacidadMax;
    private boolean activa;
}