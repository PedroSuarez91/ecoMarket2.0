package ecomarket.cupon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCupon;

    // El código es único: no pueden existir dos cupones con el mismo código
    @Column(length = 50, nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private Double porcentajeDescuento;

    @Column(nullable = false)
    private Boolean activo;

    @Column(nullable = true)
    private LocalDate fechaExpiracion;
}