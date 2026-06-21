package ecomarket.catalogo.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resenia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idResenia;

    @Column(length = 500)
    private String comentario;

    private Integer calificacion;

    private LocalDate fechaResenia;

    // Muchas resenias pertenecen a un producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    @JsonBackReference("producto-resenia")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Producto producto;
}