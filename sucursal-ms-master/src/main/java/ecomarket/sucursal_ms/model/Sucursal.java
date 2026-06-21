package ecomarket.sucursal_ms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sucursal")
public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSucursal;

    @Column(nullable = false, length = 25)
    private String direccionSucursal;

    @Column(nullable = false, length = 25)
    private String horario;

    @Column(nullable = false, length = 50)
    private String politicas;

    @Column(nullable = true)
    private Long idBodega;

}
