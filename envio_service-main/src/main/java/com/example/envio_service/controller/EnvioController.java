package com.example.envio_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.example.envio_service.model.Envio;
import com.example.envio_service.model.EstadoEnvio;
import com.example.envio_service.service.EnvioService;

@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @PostMapping
    public ResponseEntity<?> crearEnvio(@Valid @RequestBody Envio envio) {
        try {
            Envio nuevo = envioService.crearEnvio(envio);
            if (nuevo == null) {
                return new ResponseEntity<>("Pedido no encontrado", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(nuevo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear el envio", HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarEnvios() {
        List<Envio> envios = envioService.listarEnvios();
        if (envios.isEmpty()) {
            return new ResponseEntity<>("No existen envios registrados", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(envios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Envio buscado = envioService.findById(id).orElse(null);
        if (buscado == null) {
            return new ResponseEntity<>("Envio con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(buscado, HttpStatus.OK);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam EstadoEnvio estadoEnvio) {
        Envio actualizado = envioService.actualizarEstado(id, estadoEnvio);
        if (actualizado == null) {
            return new ResponseEntity<>("Envio con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEnvio(@PathVariable Long id) {
        if (envioService.eliminarEnvio(id)) {
            return new ResponseEntity<>("Envio con id " + id + " eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Envio con id " + id + " no existe", HttpStatus.NOT_FOUND);
    }
}