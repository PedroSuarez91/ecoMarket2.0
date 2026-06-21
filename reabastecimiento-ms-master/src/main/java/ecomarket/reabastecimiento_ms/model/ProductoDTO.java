package ecomarket.reabastecimiento_ms.model;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long idProducto;
    private Long idInventario;
    private String tipoProducto;
    private String nombre;
    private String descripcion;
    private String marca;
    private String precioUnitario;
    private Boolean estado;

}
