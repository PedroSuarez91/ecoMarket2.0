package ecomarket.factura_ms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecomarket.factura_ms.model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Para evitar facturar dos veces el mismo pedido
    Optional<Factura> findByIdPedido(Long idPedido);
}
