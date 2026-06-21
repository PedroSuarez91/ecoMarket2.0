package ecomarket.carro_ms.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ecomarket.carro_ms.model.Carro;
import ecomarket.carro_ms.model.CuponDescuentoDTO;
import ecomarket.carro_ms.model.ItemCarro;
import ecomarket.carro_ms.model.ProductoDTO;
import ecomarket.carro_ms.model.UsuarioDTO;
import ecomarket.carro_ms.repository.CarroRepository;

@Service
public class CarroService {
    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Carro guardarCarro(Carro carro) {
        carro.setFechaCreacion(LocalDate.now());

        String urlUsuario = "http://localhost:8081/api/v1/usuarios/" + carro.getIdUsuario();
        UsuarioDTO usuario = restTemplate.getForObject(urlUsuario, UsuarioDTO.class);
        if (usuario != null) {
            carro.setNombreUsuario(usuario.getNombre() + " " + usuario.getApellido());
        }

        double subtotalGeneral = 0.0;
        for (ItemCarro item : carro.getItems()) {
            String urlProducto = "http://localhost:8090/api/v1/productos/" + item.getIdProducto();
            ProductoDTO producto = restTemplate.getForObject(urlProducto, ProductoDTO.class);

            item.setNombreProducto(producto.getNombre());
            double precioUnitario = Double.parseDouble(producto.getPrecioUnitario());
            item.setPrecioUnitario(precioUnitario);

            double subtotalItem = precioUnitario * item.getCantidad().doubleValue();
            item.setSubtotal(subtotalItem);

            item.setCarro(carro);
            subtotalGeneral += subtotalItem;
        }

        carro.setSubtotal(subtotalGeneral);
        if (carro.getCodigoCupon() != null && !carro.getCodigoCupon().isEmpty()) {
            String urlCupon = "http://localhost:8091/api/v1/cupones/" + carro.getCodigoCupon();
            CuponDescuentoDTO cupon = restTemplate.getForObject(urlCupon, CuponDescuentoDTO.class);
            if (cupon != null && cupon.getActivo() && !cupon.getFechaExpiracion().isBefore(LocalDate.now())) {
                Double descuento = subtotalGeneral * (cupon.getPorcentajeDescuento() / 100.0);
                carro.setIdCupon(cupon.getIdCupon());
                carro.setTotal(subtotalGeneral - descuento);

            } else {
                carro.setTotal(subtotalGeneral);
            }
        } else {
            carro.setTotal(subtotalGeneral);

        }

        return carroRepository.save(carro);

    }

    public List<Carro> listarCarro() {
        return carroRepository.findAll();
    }

    public Optional<Carro> findById(Long id) {
        return carroRepository.findById(id);
    }

    public void eliminarCarro(Long id) {
        carroRepository.deleteById(id);
    }

    public Carro actualizar(Long id, Carro carro) {
        Carro existente = carroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carro no encontrado"));
        existente.setTipoEntrega(carro.getTipoEntrega());
        existente.setIdDireccion(carro.getIdDireccion());

        return carroRepository.save(existente);
    }
}
