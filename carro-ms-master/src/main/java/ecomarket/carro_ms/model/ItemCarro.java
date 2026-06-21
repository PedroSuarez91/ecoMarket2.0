package ecomarket.carro_ms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemCarro;

    private Long idProducto;
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "id_carro")
    @JsonBackReference
    private Carro carro;
}
