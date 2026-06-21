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

import ecomarket.catalogo.model.Catalogo;
import ecomarket.catalogo.service.CatalogoService;

@RestController
@RequestMapping("/api/v1/catalogos")
public class CatalogoController {

    @Autowired
    private CatalogoService catalogoService;

    @GetMapping
    public ResponseEntity<List<Catalogo>> getCatalogos() {
        List<Catalogo> catalogos = catalogoService.listarCatalogo();
        if (catalogos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(catalogos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Catalogo> getCatalogo(@PathVariable Long id) {
        Catalogo buscado = catalogoService.findById(id).orElse(null);
        if (buscado == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscado, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Catalogo> postCatalogo(@RequestBody Catalogo catalogo) {
        try {
            return new ResponseEntity<>(catalogoService.crearCatalogo(catalogo), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Catalogo> putCatalogo(@PathVariable Long id, @RequestBody Catalogo catalogo) {
        Catalogo actualizado = catalogoService.actualizarCatalogo(id, catalogo);
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatalogo(@PathVariable Long id) {
        catalogoService.eliminarCatalogo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}