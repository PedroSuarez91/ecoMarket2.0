package ecomarket.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ecomarket.catalogo.model.Catalogo;

public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {

}