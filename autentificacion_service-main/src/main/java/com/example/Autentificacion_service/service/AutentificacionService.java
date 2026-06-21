package com.example.Autentificacion_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.Autentificacion_service.model.Autentificacion;
import com.example.Autentificacion_service.model.UsuarioDTO;
import com.example.Autentificacion_service.repository.AutentificacionRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AutentificacionService {
    @Autowired
    private AutentificacionRepository autentificacionRepository;
    
    @Autowired
    private RestTemplate restTemplate;

    public String login(Autentificacion autentificacion) {
        try {
            String urlUsuario = "http://localhost:8081/api/v1/usuarios/emailUsuario/" + autentificacion.getEmailUsuario();
            UsuarioDTO usuario = restTemplate.getForObject(urlUsuario, UsuarioDTO.class);

            if (usuario == null) {
                return "Credenciales incorrectas";
            }

            if (!usuario.getPassword().equals(autentificacion.getPassword())) {
                return "Credenciales incorrectas";
            }
            
            autentificacion.setIdUsuario(usuario.getIdUsuario());
            autentificacion.setFechaLogin(LocalDateTime.now());
            autentificacionRepository.save(autentificacion);

            System.out.println("*************************");
            System.out.println(usuario);
            System.out.println("*************************");

            return "Bienvenido " + usuario.getNombre() + " " + usuario.getApellido() + ", su rol es: " + usuario.getRol();

        } catch (HttpClientErrorException.NotFound e) {
            return "Credenciales incorrectas";
        } catch (Exception e) {
            System.out.println("*************************");
            System.out.println("Usuario no disponible: " + e.getMessage());
            System.out.println("*************************");
            throw new RuntimeException("Servicio de autentificacion no disponible, intente mas tarde");
        }
    }

    public List<Autentificacion> listarLogins() {
        return autentificacionRepository.findAll();
    }
}