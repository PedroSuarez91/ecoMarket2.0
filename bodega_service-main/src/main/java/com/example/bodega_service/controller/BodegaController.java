package com.example.bodega_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bodega_service.model.Bodega;
import com.example.bodega_service.model.BodegaDTO;
import com.example.bodega_service.service.BodegaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bodega")
public class BodegaController {
    @Autowired
    private BodegaService bodegaService;

    @PostMapping
    public ResponseEntity<?> crearBodega(@Valid @RequestBody Bodega bodega) {
        try {
            return new ResponseEntity<>(bodegaService.crearBodega(bodega), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear la bodega", HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarBodegas() {
        List<Bodega> bodegas = bodegaService.listarBodegas();
        if (bodegas.isEmpty()) {
            return new ResponseEntity<>("No existen bodegas registradas", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bodegas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerBodega(@PathVariable Long id) {
        Bodega buscado = bodegaService.findById(id).orElse(null);
        if (buscado == null) {
            return new ResponseEntity<>("Bodega con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(buscado, HttpStatus.OK);
    }

    @GetMapping("/{id}/detalle")
    public ResponseEntity<?> obtenerDetalle(@PathVariable Long id) {
        BodegaDTO dto = bodegaService.obtenerBodegaDTO(id);
        if (dto == null) {
            return new ResponseEntity<>("Bodega con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarBodega(@PathVariable Long id) {
        if (bodegaService.eliminarBodega(id)) {
            return new ResponseEntity<>("Bodega con id " + id + " eliminada correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("Bodega con id " + id + " no existe", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/listar_con_capacidad")
    public ResponseEntity<?> listarBodegasCapacidad() {
        List<BodegaDTO> bodegas = bodegaService.listarBodegasDTO();
        if (bodegas.isEmpty()) {
            return new ResponseEntity<>("No existen bodegas registradas", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bodegas, HttpStatus.OK);
    }
}