package ecomarket.pedido.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecomarket.pedido.model.Direccion;


@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {

}
