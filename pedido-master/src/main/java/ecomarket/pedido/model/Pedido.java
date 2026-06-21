package ecomarket.pedido.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    private Long idUsuario;

    private String nombreCliente;

    private LocalDate fechaPedido;

    private String tipoEntrega;

    // Relacion a la entidad Direccion (gestionada dentro de pedido)
    @ManyToOne
    @JoinColumn(name = "id_direccion")
    private Direccion direccionEnvio;

    private String codigoCupon;

    private Double subtotal;

    private Double total;

    private String mensajeConfirmacion;

    private String resumenCompra;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estadoPedido;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items = new ArrayList<>();

}
