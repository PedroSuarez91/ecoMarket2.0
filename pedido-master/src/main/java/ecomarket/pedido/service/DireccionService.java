package ecomarket.pedido.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecomarket.pedido.model.Direccion;
import ecomarket.pedido.repository.DireccionRepository;


@Service
public class DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;

    public Direccion guardarDireccion(Direccion direccion) {
        return direccionRepository.save(direccion);
    }

    public List<Direccion> listarDirecciones() {
        return direccionRepository.findAll();
    }

    public Optional<Direccion> findById(Long id) {
        return direccionRepository.findById(id);
    }

    public Direccion actualizar(Long id, Direccion datos) {
        return direccionRepository.findById(id).map(direccion -> {
            direccion.setCalle(datos.getCalle());
            direccion.setNumero(datos.getNumero());
            direccion.setRegion(datos.getRegion());
            direccion.setCiudad(datos.getCiudad());
            direccion.setComuna(datos.getComuna());
            direccion.setCodigoPostal(datos.getCodigoPostal());
            return direccionRepository.save(direccion);
        }).orElse(null);
    }

    public void eliminar(Long id) {
        direccionRepository.deleteById(id);
    }
}
