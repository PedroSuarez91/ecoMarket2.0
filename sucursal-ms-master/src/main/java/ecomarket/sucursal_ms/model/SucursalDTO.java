package ecomarket.sucursal_ms.model;

import lombok.Data;

@Data
public class SucursalDTO {
    private Long idSucursal;
    private String direccionSucursal;
    private String horario;
    private String nombreBodega;
    private Integer capacidadMax;
}