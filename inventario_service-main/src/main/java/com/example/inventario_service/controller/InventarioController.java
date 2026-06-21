package com.example.inventario_service.controller;

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

import com.example.inventario_service.model.Inventario;
import com.example.inventario_service.service.InventarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/inventario")
public class InventarioController {
    @Autowired
    private InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<?> postInventario(@Valid @RequestBody Inventario inventario) {
        try {
            Inventario nuevo = inventarioService.crearInventario(inventario);
            if (nuevo == null) {
                return new ResponseEntity<>("Producto no encontrado en Catalogo", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(nuevo, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<List<Inventario>> getInventarios() {
        try {
            return ResponseEntity.ok(inventarioService.obtenerInventarios());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/stockPorBodega/{idBodega}")
        public ResponseEntity<?> getStockPorBodega(@PathVariable Long idBodega) {
        Integer total = inventarioService.getStockPorBodega(idBodega);
        return new ResponseEntity<>("Total de productos: " + total, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarInventario(@PathVariable Long id, @Valid @RequestBody Inventario inventario) {
        Inventario actualizado = inventarioService.actualizarInventario(id, inventario);
        if (actualizado == null) {
            return new ResponseEntity<>("Inventario con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @PutMapping("/descontar/{idProducto}/{cantidad}")
    public ResponseEntity<?> descontarStock(@PathVariable Long idProducto, @PathVariable Integer cantidad) {
        Inventario actualizado = inventarioService.descontarStock(idProducto, cantidad);
        if (actualizado == null) {
            return new ResponseEntity<>("Stock insuficiente o producto no encontrado", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @GetMapping("/alertas")
    public ResponseEntity<?> alertasStockMinimo() {
        List<Inventario> alertas = inventarioService.alertasStockMinimo();
        if (alertas.isEmpty()) {
            return new ResponseEntity<>("No hay productos bajo stock minimo", HttpStatus.OK);
        }
        return new ResponseEntity<>(alertas, HttpStatus.OK);
    }
}