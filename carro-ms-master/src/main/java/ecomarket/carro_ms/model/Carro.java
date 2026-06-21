package ecomarket.carro_ms.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class Carro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarro;

    private Long idUsuario;

    private String nombreUsuario;

    private LocalDate fechaCreacion;

    private Double subtotal;

    private Double total;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long idCupon;

    private String codigoCupon;

    private String tipoEntrega;

    private Long idDireccion;

    @OneToMany(mappedBy = "carro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarro> items = new ArrayList<>();

}
