package ecomarket.pedido.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ecomarket.pedido.model.CarroDTO;
import ecomarket.pedido.model.Direccion;
import ecomarket.pedido.model.EstadoPedido;
import ecomarket.pedido.model.ItemCarroDTO;
import ecomarket.pedido.model.ItemPedido;
import ecomarket.pedido.model.Pedido;
import ecomarket.pedido.repository.DireccionRepository;
import ecomarket.pedido.repository.PedidoRepository;


@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${servicios.carro.url}")
    private String carroUrl;

    @Value("${servicios.inventario.url}")
    private String inventarioUrl;

    public Pedido crearPedidoDesdeCarro(Long idCarro, Long idDireccion) {
        String urlCarro = carroUrl + "/api/v1/carros/" + idCarro;
        CarroDTO carro = restTemplate.getForObject(urlCarro, CarroDTO.class);
        if (carro == null || carro.getItems() == null || carro.getItems().isEmpty()) {
            return null;
        }
        Pedido pedido = new Pedido();
        pedido.setIdUsuario(carro.getIdUsuario());
        pedido.setNombreCliente(carro.getNombreUsuario());
        pedido.setTipoEntrega(carro.getTipoEntrega());
        pedido.setCodigoCupon(carro.getCodigoCupon());
        pedido.setSubtotal(carro.getSubtotal());
        pedido.setTotal(carro.getTotal());
        if (idDireccion != null) {
            Direccion direccion = direccionRepository.findById(idDireccion).orElse(null);
            pedido.setDireccionEnvio(direccion);
        }
        List<ItemPedido> items = new ArrayList<>();
        for (ItemCarroDTO itemCarro : carro.getItems()) {
            ItemPedido item = new ItemPedido();
            item.setIdProducto(itemCarro.getIdProducto());
            item.setNombreProducto(itemCarro.getNombreProducto());
            item.setCantidad(itemCarro.getCantidad());
            item.setPrecioUnitario(itemCarro.getPrecioUnitario());
            item.setSubtotal(itemCarro.getSubtotal());
            item.setPedido(pedido);
            items.add(item);
            descontarStock(item.getIdProducto(), item.getCantidad());
        }
        pedido.setItems(items);
        pedido.setFechaPedido(LocalDate.now());
        pedido.setEstadoPedido(EstadoPedido.PENDIENTE);
        pedido.setMensajeConfirmacion("Pedido generado a partir del carro " + idCarro);
        pedido.setResumenCompra("Items: " + items.size()
                + " | Subtotal: " + carro.getSubtotal()
                + " | Total: " + carro.getTotal());
        Pedido guardado = pedidoRepository.save(pedido);
        return guardado;
    }


    public Pedido actualizarDireccion(Long idPedido, Long idDireccion) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElse(null);
        if (pedido == null) {
            return null;
        }
        Direccion direccion = direccionRepository.findById(idDireccion).orElse(null);
        if (direccion == null) {
            return null; 
        }
        pedido.setDireccionEnvio(direccion);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPorUsuario(Long idUsuario) {
        return pedidoRepository.findByIdUsuario(idUsuario);
    }

    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido actualizarEstado(Long id, EstadoPedido estado) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setEstadoPedido(estado);
            return pedidoRepository.save(pedido);
        }).orElse(null);
    }

    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }


    private void descontarStock(Long idProducto, Integer cantidad) {
        try {
            String url = inventarioUrl + "/api/v1/inventarios/descontar/" + idProducto + "/" + cantidad;
            restTemplate.put(url, null);
        } catch (Exception e) {
            System.out.println("AVISO: no se pudo descontar stock del producto " + idProducto
                    + " (servicio inventario no disponible): " + e.getMessage());
        }
    }


}
