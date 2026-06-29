package ecomarket.factura_ms.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ecomarket.factura_ms.model.Factura;
import ecomarket.factura_ms.service.FacturaService;

@RestController
@RequestMapping("/api/v1/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @GetMapping
    public ResponseEntity<List<Factura>> getFacturas() {
        List<Factura> facturas = facturaService.listar();
        if (facturas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(facturas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> getFactura(@PathVariable Long id) {
        Optional<Factura> buscada = facturaService.findById(id);
        if (buscada.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscada.get(), HttpStatus.OK);
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<Factura> getPorPedido(@PathVariable Long idPedido) {
        Optional<Factura> buscada = facturaService.findByPedido(idPedido);
        if (buscada.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscada.get(), HttpStatus.OK);
    }

    // Emitir factura a partir de un pedido existente
    @PostMapping("/emitir/{idPedido}")
    public ResponseEntity<Factura> emitir(@PathVariable Long idPedido) {
        // No se factura dos veces el mismo pedido
        if (facturaService.existePorPedido(idPedido)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Factura factura = facturaService.emitirFactura(idPedido);
        if (factura == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // el pedido no existe
        }
        return new ResponseEntity<>(factura, HttpStatus.CREATED);
    }
}
