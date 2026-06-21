package com.example.soporte_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.soporte_service.model.Soporte;
import com.example.soporte_service.model.UsuarioDTO;
import com.example.soporte_service.repository.SoporteRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class SoporteService {
    @Autowired
    private SoporteRepository soporteRepository;
    @Autowired
    private RestTemplate restTemplate;

    public Soporte crearTicket(Soporte soporte) {
        try {
            // Validar que el usuario exista en el MS Usuario
            String urlUsuario = "http://localhost:8081/api/v1/usuarios/" + soporte.getIdUsuario();
            UsuarioDTO usuario = restTemplate.getForObject(urlUsuario, UsuarioDTO.class);

            if (usuario == null) {
                return null;
            }

            soporte.setEstado(true); // true = ticket abierto

            return soporteRepository.save(soporte);

        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            System.out.println("*************************");
            System.out.println("Usuario no disponible: " + e.getMessage());
            System.out.println("*************************");
            throw new RuntimeException("Servicio de Usuario no disponible, intente mas tarde");
        }
    }

    public Soporte cerrarTicket(Long id) {
        Soporte buscado = soporteRepository.findById(id).orElse(null);
        if (buscado == null) return null;

        buscado.setEstado(false); // false = ticket cerrado
        return soporteRepository.save(buscado);
    }

    public List<Soporte> listarSoportes() {
        return soporteRepository.findAll();
    }

    public Soporte findById(Long id) {
        return soporteRepository.findById(id).orElse(null);
    }
}