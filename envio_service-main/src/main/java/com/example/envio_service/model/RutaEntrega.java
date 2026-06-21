package com.example.envio_service.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rutas_entrega")
public class RutaEntrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRuta;

    @NotBlank(message = "La comuna de destino es obligatoria")
    @Column(nullable = false)
    private String comunaDestino;

    @ElementCollection
    @CollectionTable(name = "ruta_envios", joinColumns = @JoinColumn(name = "id_ruta"))
    @Column(name = "id_envio")
    @NotEmpty(message = "La ruta debe incluir al menos un envio")
    private List<Long> enviosIds;
}