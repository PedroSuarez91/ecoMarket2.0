package ecomarket.catalogo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ecomarket.catalogo.model.Resenia;

public interface ReseniaRepository extends JpaRepository<Resenia, Long> {

    // Resenias de un producto en particular
    List<Resenia> findByProducto_IdProducto(Long idProducto);
}
