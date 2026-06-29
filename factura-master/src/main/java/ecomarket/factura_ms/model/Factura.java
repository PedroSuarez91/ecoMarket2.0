package ecomarket.factura_ms.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFactura;

    private Long idPedido;

    private String nombreCliente;

    private LocalDate fechaEmision;

    // Desglose de factura electronica (total = neto + iva)
    private Integer neto;

    private Integer iva;

    private Integer total;
}
