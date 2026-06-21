package ecomarket.catalogo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecomarket.catalogo.model.Producto;
import ecomarket.catalogo.repository.ProductoRepository;


@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public Producto registrarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findByIdProducto(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> findByCategoria(Long idCategoria) {
        return productoRepository.findByCategorias_IdCategoria(idCategoria);
    }

    public Producto actualizarProducto(Long id, Producto datos) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(datos.getNombre());
            producto.setDescripcion(datos.getDescripcion());
            producto.setTipoProducto(datos.getTipoProducto());
            producto.setMarca(datos.getMarca());
            producto.setPrecioUnitario(datos.getPrecioUnitario());
            producto.setEstado(datos.getEstado());
            producto.setIdInventario(datos.getIdInventario());
            producto.setCategorias(datos.getCategorias());
            return productoRepository.save(producto);
        }).orElse(null);
    }

    public List<Producto> findByRangoPrecio(Integer minimo, Integer maximo) {
        return productoRepository.findByPrecioUnitarioBetween(minimo, maximo);
    }

    public List<Producto> findByPrecioMaximo(Integer maximo) {
        return productoRepository.findByPrecioUnitarioLessThanEqual(maximo);
    }

    public List<Producto> findByPrecioMinimo(Integer minimo) {
        return productoRepository.findByPrecioUnitarioGreaterThanEqual(minimo);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> findByMarca(String marca) {
        return productoRepository.findByMarca(marca);
    }

}
