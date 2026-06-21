package ecomarket.cupon.controller;

import ecomarket.cupon.model.Cupon;
import ecomarket.cupon.service.CuponService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/cupones")
public class CuponController {
    @Autowired
    private CuponService cuponService;

    @PostMapping
    public ResponseEntity<Cupon> guardar(@RequestBody Cupon cupon) {
        try {
            return new ResponseEntity<>(cuponService.guardar(cupon), HttpStatus.CREATED);
        } catch (Exception e) {
            // Si el código ya existe (es único), cae aquí
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<List<Cupon>> listar() {
        List<Cupon> cupones = cuponService.listar();
        if (cupones.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(cupones, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cupon> obtener(@PathVariable Long id) {
        Cupon c = cuponService.findById(id).orElse(null);
        if (c == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(c, HttpStatus.OK);
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Cupon> buscarPorCodigo(@PathVariable String codigo) {
        Cupon c = cuponService.buscarPorCodigo(codigo).orElse(null);
        if (c == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(c, HttpStatus.OK);
    }

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<Boolean> validar(@PathVariable String codigo) {
        return new ResponseEntity<>(cuponService.validar(codigo), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cupon> actualizar(@PathVariable Long id, @RequestBody Cupon datos) {
        Cupon actualizado = cuponService.actualizar(id, datos);
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cuponService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
