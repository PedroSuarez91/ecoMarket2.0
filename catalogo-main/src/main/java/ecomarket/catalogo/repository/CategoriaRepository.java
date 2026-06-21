package ecomarket.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ecomarket.catalogo.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}