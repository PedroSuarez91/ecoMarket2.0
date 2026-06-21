package ecomarket.reabastecimiento_ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecomarket.reabastecimiento_ms.model.Reabastecimiento;

@Repository
public interface ReabastecimientoRepository extends JpaRepository<Reabastecimiento, Long> {

}