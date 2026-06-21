package com.example.envio_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.envio_service.model.RutaEntrega;
import com.example.envio_service.repository.RutaEntregaRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RutaEntregaService {

    @Autowired
    private RutaEntregaRepository rutaEntregaRepository;

    public RutaEntrega crearRuta(RutaEntrega ruta) {
        return rutaEntregaRepository.save(ruta);
    }

    public List<RutaEntrega> listarRutas() {
        return rutaEntregaRepository.findAll();
    }

    public Optional<RutaEntrega> findById(Long id) {
        return rutaEntregaRepository.findById(id);
    }

    public RutaEntrega actualizarRuta(Long id, RutaEntrega datos) {
        RutaEntrega buscada = rutaEntregaRepository.findById(id).orElse(null);
        if (buscada == null) return null;

        buscada.setComunaDestino(datos.getComunaDestino());
        buscada.setEnviosIds(datos.getEnviosIds());
        return rutaEntregaRepository.save(buscada);
    }

    public boolean eliminarRuta(Long id) {
        if (rutaEntregaRepository.existsById(id)) {
            rutaEntregaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}