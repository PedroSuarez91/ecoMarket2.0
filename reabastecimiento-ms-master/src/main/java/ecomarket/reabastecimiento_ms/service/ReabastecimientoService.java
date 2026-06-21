package ecomarket.reabastecimiento_ms.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ecomarket.reabastecimiento_ms.model.ItemReabastecimiento;
import ecomarket.reabastecimiento_ms.model.ProductoDTO;
import ecomarket.reabastecimiento_ms.model.ProveedorDTO;
import ecomarket.reabastecimiento_ms.model.Reabastecimiento;
import ecomarket.reabastecimiento_ms.repository.ReabastecimientoRepository;

@Service
public class ReabastecimientoService {
    @Autowired
    private ReabastecimientoRepository reabastecimientoRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Reabastecimiento guardarReabastecimiento(Reabastecimiento reabastecimiento) {
        reabastecimiento.setFecha(LocalDate.now());

        if (reabastecimiento.getEstado() == null || reabastecimiento.getEstado().isEmpty()) {
            reabastecimiento.setEstado("PENDIENTE");
        }

        String urlProveedor = "http://localhost:8082/api/v1/proveedores/" + reabastecimiento.getIdProveedor();
        ProveedorDTO proveedor = restTemplate.getForObject(urlProveedor, ProveedorDTO.class);
        if (proveedor == null) {
            throw new RuntimeException("Proveedor no encontrado");
        }

        for (ItemReabastecimiento item : reabastecimiento.getItems()) {
            String urlProducto = "http://localhost:8090/api/v1/productos/" + item.getIdProducto();
            ProductoDTO producto = restTemplate.getForObject(urlProducto, ProductoDTO.class);
            if (producto == null) {
                throw new RuntimeException("Producto no encontrado: " + item.getIdProducto());
            }
            item.setReabastecimiento(reabastecimiento);
        }

        return reabastecimientoRepository.save(reabastecimiento);
    }

    public List<Reabastecimiento> listarReabastecimiento() {
        return reabastecimientoRepository.findAll();
    }

    public Optional<Reabastecimiento> findById(Long id) {
        return reabastecimientoRepository.findById(id);
    }
}
