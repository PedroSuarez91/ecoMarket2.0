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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ecomarket.pedido.model.EstadoPedido;
import ecomarket.pedido.model.Pedido;
import ecomarket.pedido.service.PedidoService;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<Pedido>> getPedidos() {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedido(@PathVariable Long id) {
        Optional<Pedido> buscado = pedidoService.findById(id);
        if (buscado.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscado.get(), HttpStatus.OK);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Pedido>> getPorUsuario(@PathVariable Long idUsuario) {
        List<Pedido> pedidos = pedidoService.listarPorUsuario(idUsuario);
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    // Crea el pedido desde un carro; la direccion se pasa por su id (entidad propia)
    @PostMapping("/carro/{idCarro}")
    public ResponseEntity<Pedido> crearDesdeCarro(@PathVariable Long idCarro,
                                                  @RequestParam(required = false) Long idDireccion) {
        Pedido nuevo = pedidoService.crearPedidoDesdeCarro(idCarro, idDireccion);
        if (nuevo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    // Asigna o cambia la direccion de un pedido existente
    @PutMapping("/{id}/direccion/{idDireccion}")
    public ResponseEntity<Pedido> asignarDireccion(@PathVariable Long id, @PathVariable Long idDireccion) {
        Pedido actualizado = pedidoService.actualizarDireccion(id, idDireccion);
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long id, @RequestParam EstadoPedido estado) {
        Pedido actualizado = pedidoService.actualizarEstado(id, estado);
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        try {
            pedidoService.eliminarPedido(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
