package com.example.soporte_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.soporte_service.model.Soporte;
import com.example.soporte_service.service.SoporteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/soporte")
public class SoporteController {
    @Autowired
    private SoporteService soporteService;

    @PostMapping
    public ResponseEntity<?> crearTicket(@Valid @RequestBody Soporte soporte) {
        try {
            Soporte nuevo = soporteService.crearTicket(soporte);
            if (nuevo == null) {
                return new ResponseEntity<>("Usuario no encontrado, no se puede crear el ticket", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(nuevo, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarSoportes() {
        List<Soporte> soportes = soporteService.listarSoportes();
        if (soportes.isEmpty()) {
            return new ResponseEntity<>("No existen tickets de soporte registrados", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(soportes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerSoporte(@PathVariable Long id) {
        Soporte buscado = soporteService.findById(id);
        if (buscado == null) {
            return new ResponseEntity<>("Ticket con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(buscado, HttpStatus.OK);
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<?> cerrarTicket(@PathVariable Long id) {
        Soporte cerrado = soporteService.cerrarTicket(id);
        if (cerrado == null) {
            return new ResponseEntity<>("Ticket con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cerrado, HttpStatus.OK);
    }
}