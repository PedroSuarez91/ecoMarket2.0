package ecomarket.pedido.controller;

import java.util.List;
import java.util.Optional;

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

import ecomarket.pedido.model.Direccion;
import ecomarket.pedido.service.DireccionService;


@RestController
@RequestMapping("/api/v1/direcciones")
public class DireccionController {

    @Autowired
    private DireccionService direccionService;

    @GetMapping
    public ResponseEntity<List<Direccion>> getDirecciones() {
        List<Direccion> direcciones = direccionService.listarDirecciones();
        if (direcciones.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(direcciones, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Direccion> getDireccion(@PathVariable Long id) {
        Optional<Direccion> buscada = direccionService.findById(id);
        if (buscada.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscada.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Direccion> postDireccion(@RequestBody Direccion direccion) {
        try {
            return new ResponseEntity<>(direccionService.guardarDireccion(direccion), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Direccion> putDireccion(@PathVariable Long id, @RequestBody Direccion direccion) {
        Direccion actualizada = direccionService.actualizar(id, direccion);
        if (actualizada == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDireccion(@PathVariable Long id) {
        direccionService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
