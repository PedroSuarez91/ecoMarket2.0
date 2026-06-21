package ecomarket.pedido.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarroDTO {
    private Long idCarro;
    private Long idUsuario;
    private String nombreUsuario;
    private LocalDate fechaCreacion;
    private Double subtotal;
    private Double total;
    private String codigoCupon;
    private String tipoEntrega;
    private Long idDireccion;
    private List<ItemCarroDTO> items;
}
