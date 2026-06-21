package ecomarket.reabastecimiento_ms.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ecomarket.reabastecimiento_ms.model.Reabastecimiento;
import ecomarket.reabastecimiento_ms.service.ReabastecimientoService;

@RestController
@RequestMapping("/api/v1/reabastecimientos")
public class ReabastecimientoController {

    @Autowired
    private ReabastecimientoService reabastecimientoService;

    @GetMapping
    public ResponseEntity<List<Reabastecimiento>> getReabastecimientos() {

        List<Reabastecimiento> reabastecimientos = reabastecimientoService.listarReabastecimiento();

        if (reabastecimientos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(reabastecimientos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Reabastecimiento> postReabastecimiento(@RequestBody Reabastecimiento reabastecimiento) {

        Reabastecimiento nuevo;

        try {
            nuevo = reabastecimientoService.guardarReabastecimiento(reabastecimiento);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);

        } catch (Exception e) {
            System.out.println("Error al crear reabastecimiento: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reabastecimiento> getReabastecimiento(@PathVariable Long id) {

        Optional<Reabastecimiento> buscado = reabastecimientoService.findById(id);

        if (buscado.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(buscado.get(), HttpStatus.OK);
    }
}