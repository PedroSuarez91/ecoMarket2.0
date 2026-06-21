package ecomarket.catalogo.controller;

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

import ecomarket.catalogo.model.Resenia;
import ecomarket.catalogo.service.ReseniaService;


@RestController
@RequestMapping("/api/v1/resenias")
public class ReseniaController {

    @Autowired
    private ReseniaService reseniaService;

    @GetMapping
    public ResponseEntity<List<Resenia>> getResenias() {
        List<Resenia> resenias = reseniaService.listarResenias();
        if (resenias.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(resenias, HttpStatus.OK);
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<Resenia>> getPorProducto(@PathVariable Long idProducto) {
        List<Resenia> resenias = reseniaService.listarPorProducto(idProducto);
        if (resenias.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(resenias, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Resenia> postResenia(@RequestBody Resenia resenia) {
        Resenia nueva = reseniaService.registrarResenia(resenia);
        if (nueva == null) {
            // el producto referenciado no existe o no se envio
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(nueva, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resenia> putResenia(@PathVariable Long id, @RequestBody Resenia resenia) {
        Resenia actualizada = reseniaService.actualizarResenia(id, resenia);
        if (actualizada == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResenia(@PathVariable Long id) {
        reseniaService.eliminarResenia(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}