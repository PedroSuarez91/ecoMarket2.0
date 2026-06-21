package ecomarket.carro_ms.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CuponDescuentoDTO {
    private Long idCupon;
    private String codigo;
    private Double porcentajeDescuento;
    private Boolean activo;
    private LocalDate fechaExpiracion;
}
