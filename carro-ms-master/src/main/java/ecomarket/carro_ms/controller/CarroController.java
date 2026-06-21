package ecomarket.carro_ms.controller;

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
import ecomarket.carro_ms.model.Carro;
import ecomarket.carro_ms.service.CarroService;

@RestController
@RequestMapping("/api/v1/carros")
public class CarroController {

    @Autowired
    private CarroService carroService;

    @GetMapping
    public ResponseEntity<List<Carro>> getCarros() {

        List<Carro> carros = carroService.listarCarro();

        if (carros.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(carros, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Carro> postCarro(@RequestBody Carro carro) {

        Carro nuevo;

        try {
            nuevo = carroService.guardarCarro(carro);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carro> getCarro(@PathVariable Long id) {

        java.util.Optional<Carro> buscado = carroService.findById(id);

        if (buscado.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(buscado.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCarro(@PathVariable Long id) {

        try {
            carroService.eliminarCarro(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carro> actualizarCarro(@PathVariable Long id, @RequestBody Carro carro) {

        try {
            Carro actualizado = carroService.actualizar(id, carro);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
