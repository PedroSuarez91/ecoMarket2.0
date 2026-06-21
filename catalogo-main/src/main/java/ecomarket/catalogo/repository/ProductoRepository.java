package ecomarket.catalogo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ecomarket.catalogo.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByMarca(String marca);

    // buscarPorNombre() -> 
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // findByRangoPrecio()
    List<Producto> findByPrecioUnitarioBetween(Integer minimo, Integer maximo);

    // findByPrecioMaximo() -> productos con precio menor o igual al tope
    List<Producto> findByPrecioUnitarioLessThanEqual(Integer maximo);

    // findByPrecioMinimo() -> productos con precio mayor o igual al piso
    List<Producto> findByPrecioUnitarioGreaterThanEqual(Integer minimo);

    // findByCategoria() -> productos que pertenecen a una categoria dada
    List<Producto> findByCategorias_IdCategoria(Long idCategoria);
}
