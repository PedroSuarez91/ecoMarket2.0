package ecomarket.cupon.repository;

import ecomarket.cupon.model.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuponRepository extends JpaRepository<Cupon, Long> {

    // buscarPorCodigo(): devuelve el cupón con ese código (es único, por eso Optional)
    Optional<Cupon> findByCodigo(String codigo);
}