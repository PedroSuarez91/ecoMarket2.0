package ecomarket.catalogo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCatalogo;

    @Column(length = 100, nullable = false)
    private String nombreCatalogo;

    @Column(nullable = false)
    private LocalDate fechaActualizacion;

    // Un catalogo agrupa muchos productos
    @OneToMany(mappedBy = "catalogo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("catalogo-producto")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Producto> productos = new ArrayList<>();
}