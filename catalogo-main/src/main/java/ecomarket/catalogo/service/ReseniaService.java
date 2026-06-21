package ecomarket.catalogo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecomarket.catalogo.model.Producto;
import ecomarket.catalogo.model.Resenia;
import ecomarket.catalogo.repository.ProductoRepository;
import ecomarket.catalogo.repository.ReseniaRepository;


@Service
public class ReseniaService {

    @Autowired
    private ReseniaRepository reseniaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public Resenia registrarResenia(Resenia resenia) {
        // 1. La resenia debe venir con un producto y su id
        if (resenia.getProducto() == null || resenia.getProducto().getIdProducto() == null) {
            return null; // falta el producto -> no se guarda
        }
        // 2. El producto referenciado debe existir en la base
        Producto producto = productoRepository.findById(resenia.getProducto().getIdProducto()).orElse(null);
        if (producto == null) {
            return null; // el producto no existe -> no se guarda
        }
        // 3. Todo correcto: asocia el producto real y guarda
        resenia.setProducto(producto);
        return reseniaRepository.save(resenia);
    }

    public List<Resenia> listarResenias() {
        return reseniaRepository.findAll();
    }

    public List<Resenia> listarPorProducto(Long idProducto) {
        return reseniaRepository.findByProducto_IdProducto(idProducto);
    }

    public Optional<Resenia> findById(Long id) {
        return reseniaRepository.findById(id);
    }

    public Resenia actualizarResenia(Long id, Resenia datos) {
        return reseniaRepository.findById(id).map(resenia -> {
            resenia.setComentario(datos.getComentario());
            resenia.setCalificacion(datos.getCalificacion());
            resenia.setFechaResenia(datos.getFechaResenia());
            return reseniaRepository.save(resenia);
        }).orElse(null);
    }

    public void eliminarResenia(Long id) {
        reseniaRepository.deleteById(id);
    }
}