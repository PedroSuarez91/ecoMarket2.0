package ecomarket.proveedor_ms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecomarket.proveedor_ms.model.Proveedor;
import ecomarket.proveedor_ms.repository.ProveedorRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProveedorService {
    @Autowired
    private ProveedorRepository proveedorRepository;

    public Proveedor guardarProveedor(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public List<Proveedor> listar() {
        return proveedorRepository.findAll();
    }

    public Proveedor findById(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado: " + id));
    }

    public Proveedor modificar(Long id, Proveedor proveedor) {
        Proveedor proveedorExistente = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado: " + id));
        proveedorExistente.setNombre(proveedor.getNombre());
        proveedorExistente.setRut(proveedor.getRut());
        return proveedorRepository.save(proveedorExistente);
    }

    public void eliminar(Long id) {
        proveedorRepository.deleteById(id);

    }
}
