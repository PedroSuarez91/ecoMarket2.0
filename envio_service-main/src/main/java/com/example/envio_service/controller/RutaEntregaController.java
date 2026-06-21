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
import org.springframework.web.bind.annotation.RestController;

import com.example.envio_service.model.RutaEntrega;
import com.example.envio_service.service.RutaEntregaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/rutas")
public class RutaEntregaController {

    @Autowired
    private RutaEntregaService rutaEntregaService;

    @PostMapping
    public ResponseEntity<?> crearRuta(@Valid @RequestBody RutaEntrega ruta) {
        try {
            return new ResponseEntity<>(rutaEntregaService.crearRuta(ruta), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear la ruta", HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarRutas() {
        List<RutaEntrega> rutas = rutaEntregaService.listarRutas();
        if (rutas.isEmpty()) {
            return new ResponseEntity<>("No existen rutas registradas", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(rutas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        RutaEntrega buscada = rutaEntregaService.findById(id).orElse(null);
        if (buscada == null) {
            return new ResponseEntity<>("Ruta con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(buscada, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRuta(@PathVariable Long id, @Valid @RequestBody RutaEntrega datos) {
        RutaEntrega actualizada = rutaEntregaService.actualizarRuta(id, datos);
        if (actualizada == null) {
            return new ResponseEntity<>("Ruta con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRuta(@PathVariable Long id) {
        if (rutaEntregaService.eliminarRuta(id)) {
            return new ResponseEntity<>("Ruta con id " + id + " eliminada correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Ruta con id " + id + " no existe", HttpStatus.NOT_FOUND);
    }
}