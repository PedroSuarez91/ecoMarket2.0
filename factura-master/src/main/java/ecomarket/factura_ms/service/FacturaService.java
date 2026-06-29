package ecomarket.factura_ms.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ecomarket.factura_ms.model.Factura;
import ecomarket.factura_ms.model.PedidoDTO;
import ecomarket.factura_ms.repository.FacturaRepository;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${servicios.pedido.url}")
    private String pedidoUrl;

    // emitirFacturaElectronica(): genera la factura a partir de un pedido
    public Factura emitirFactura(Long idPedido) {
        // 1. Traer el pedido desde su microservicio
        String url = pedidoUrl + "/api/v1/pedidos/" + idPedido;
        PedidoDTO pedido = restTemplate.getForObject(url, PedidoDTO.class);
        if (pedido == null || pedido.getTotal() == null) {
            return null; // el pedido no existe
        }

        // 2. Calcular neto e IVA (el total ya viene con IVA incluido, 19%)
        int total = (int) Math.round(pedido.getTotal());
        int neto = (int) Math.round(total / 1.19);
        int iva = total - neto;

        // 3. Armar y guardar la factura
        Factura factura = new Factura();
        factura.setIdPedido(idPedido);
        factura.setNombreCliente(pedido.getNombreCliente());
        factura.setFechaEmision(LocalDate.now());
        factura.setNeto(neto);
        factura.setIva(iva);
        factura.setTotal(total);
        return facturaRepository.save(factura);
    }

    public boolean existePorPedido(Long idPedido) {
        return facturaRepository.findByIdPedido(idPedido).isPresent();
    }

    public List<Factura> listar() {
        return facturaRepository.findAll();
    }

    public Optional<Factura> findById(Long id) {
        return facturaRepository.findById(id);
    }

    public Optional<Factura> findByPedido(Long idPedido) {
        return facturaRepository.findByIdPedido(idPedido);
    }
}
