package ecomarket.reabastecimiento_ms.model;

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
public class ItemReabastecimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemReabastecimiento;

    private Long idProducto;

    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "id_reabastecimiento")
    @JsonBackReference
    private Reabastecimiento reabastecimiento;
}
