package ecomarket.catalogo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecomarket.catalogo.model.Catalogo;
import ecomarket.catalogo.repository.CatalogoRepository;


@Service
public class CatalogoService {
    @Autowired
    private CatalogoRepository catalogoRepository;

    public Catalogo crearCatalogo(Catalogo catalogo) {
        return catalogoRepository.save(catalogo);
    }

    public List<Catalogo> listarCatalogo() {
        return catalogoRepository.findAll();
    }

    public Optional<Catalogo> findById(Long id) {
        return catalogoRepository.findById(id);
    }

    public Catalogo actualizarCatalogo(Long id, Catalogo datos) {
        return catalogoRepository.findById(id).map(catalogo -> {
            catalogo.setNombreCatalogo(datos.getNombreCatalogo());
            catalogo.setFechaActualizacion(datos.getFechaActualizacion());
            return catalogoRepository.save(catalogo);
        }).orElse(null);
    }

    public void eliminarCatalogo(Long id) {
        catalogoRepository.deleteById(id);
    }
}