package com.example.envio_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.envio_service.model.Envio;
import com.example.envio_service.model.EstadoEnvio;
import com.example.envio_service.model.PedidoDTO;
import com.example.envio_service.repository.EnvioRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Envio crearEnvio(Envio envio) {
        try {
            // Ajusta el puerto al de tu microservicio de Pedidos
            String urlPedido = "http://localhost:8093/api/v1/pedidos/" + envio.getIdPedido();
            PedidoDTO pedido = restTemplate.getForObject(urlPedido, PedidoDTO.class);

            if (pedido != null) {
                envio.setDireccionEnvio(pedido.getDireccionEnvio());
                envio.setFechaEnvio(LocalDate.now());
                if (envio.getEstadoEnvio() == null) {
                    envio.setEstadoEnvio(EstadoEnvio.PREPARANDO);
                }
                return envioRepository.save(envio);
            }
            return null;
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            System.out.println("Pedido no disponible: " + e.getMessage());
            return null;
        }
    }

    public List<Envio> listarEnvios() {
        return envioRepository.findAll();
    }

    public Optional<Envio> findById(Long id) {
        return envioRepository.findById(id);
    }

    public Envio actualizarEstado(Long id, EstadoEnvio estadoEnvio) {
        Envio buscado = envioRepository.findById(id).orElse(null);
        if (buscado == null) return null;

        buscado.setEstadoEnvio(estadoEnvio);
        if (estadoEnvio == EstadoEnvio.ENTREGADO) {
            buscado.setFechaEntrega(LocalDate.now());
        }
        return envioRepository.save(buscado);
    }

    public boolean eliminarEnvio(Long id) {
        if (envioRepository.existsById(id)) {
            envioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}