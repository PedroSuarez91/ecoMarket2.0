package ecomarket.reabastecimiento_ms.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reabastecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedidoReabastecimiento;

    private Long idProveedor;

    private LocalDate fecha;

    private String estado;

    @OneToMany(mappedBy = "reabastecimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemReabastecimiento> items = new ArrayList<>();

}
