package com.example.envio_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PedidoDTO {

    @Setter
    private Long idPedido;

    @Setter
    private String nombreCliente;

    // Queda como texto; el EnvioService lo lee con getDireccionEnvio()
    private String direccionEnvio;

    // Jackson llama a este setter con el OBJETO direccionEnvio del JSON
    // y aqui lo aplanamos a un String.
    @JsonProperty("direccionEnvio")
    public void setDireccionEnvio(Direccion d) {
        if (d != null) {
            this.direccionEnvio = d.getCalle() + " " + d.getNumero()
                    + ", " + d.getComuna() + ", " + d.getCiudad();
        }
    }

    // Clase interna que refleja el objeto direccion que manda el pedido
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Direccion {
        private String calle;
        private String numero;
        private String region;
        private String ciudad;
        private String comuna;
        private Integer codigoPostal;
    }
}