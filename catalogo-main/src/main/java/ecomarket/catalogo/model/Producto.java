package ecomarket.catalogo.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    // Referencia al microservicio Inventario (no es una relacion JPA,
    // es un puntero entre servicios, igual que Reserva.idCliente en la base).
    private Long idInventario;

    @Column(length = 50)
    private String tipoProducto;

    @Column(length = 150, nullable = false)
    private String nombre;

    @Column(length = 150, nullable = false)
    private String marca;

    @Column(length = 500)
    private String descripcion;

    private Integer precioUnitario;

    private Boolean estado;

    // Muchos productos pertenecen a un catalogo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_catalogo")
    @JsonBackReference("catalogo-producto")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Catalogo catalogo;

    // Un producto tiene muchas resenias
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("producto-resenia")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Resenia> resenias = new ArrayList<>();

    // Relacion muchos-a-muchos con Categoria (lado dueño)
    @ManyToMany
    @JoinTable(name = "producto_categoria",
            joinColumns = @JoinColumn(name = "id_producto"),
            inverseJoinColumns = @JoinColumn(name = "id_categoria"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Categoria> categorias = new ArrayList<>();
}
