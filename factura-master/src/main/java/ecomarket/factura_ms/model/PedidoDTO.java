package ecomarket.factura_ms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PedidoDTO {
    private Long idPedido;
    private String nombreCliente;
    private Double total;
}
