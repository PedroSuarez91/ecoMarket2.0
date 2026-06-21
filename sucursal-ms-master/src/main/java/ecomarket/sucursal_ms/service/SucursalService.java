package ecomarket.sucursal_ms.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ecomarket.sucursal_ms.model.BodegaDTO;
import ecomarket.sucursal_ms.model.Sucursal;
import ecomarket.sucursal_ms.model.SucursalDTO;
import ecomarket.sucursal_ms.repository.SucursalRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class SucursalService {
    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Sucursal crearSucursal(Sucursal sucursal) {

        System.out.println(sucursal);

        return sucursalRepository.save(sucursal);
    }

    public List<Sucursal> listarSucursales() {
        return sucursalRepository.findAll();
    }

    public Optional<Sucursal> findById(Long id) {
        return sucursalRepository.findById(id);
    }

    public boolean eliminarSucursal(Long id) {
        if (sucursalRepository.existsById(id)) {
            sucursalRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public SucursalDTO obtenerSucursalDTO(Long idSucursal) {
        Sucursal sucursal = sucursalRepository.findById(idSucursal).orElse(null);
        if (sucursal == null)
            return null;

        SucursalDTO dto = new SucursalDTO();
        dto.setIdSucursal(sucursal.getIdSucursal());
        dto.setDireccionSucursal(sucursal.getDireccionSucursal());
        dto.setHorario(sucursal.getHorario());

        try {
            String urlBodega = "http://localhost:9094/api/v1/bodega/" + sucursal.getIdBodega();
            BodegaDTO bodega = restTemplate.getForObject(urlBodega, BodegaDTO.class);
            if (bodega != null) {
                dto.setNombreBodega(bodega.getNombreBodega());
                dto.setCapacidadMax(bodega.getCapacidadMax());
            }
        } catch (Exception e) {
            System.out.println("Bodega no disponible: " + e.getMessage());
        }
        return dto;
    }

    public String consultarStock(Long idSucursal) {
        Sucursal sucursal = sucursalRepository.findById(idSucursal).orElse(null);
        if (sucursal == null)
            return null;

        try {
            String urlInventario = "http://localhost:9093/api/v1/inventario/stockPorBodega/" + sucursal.getIdBodega();
            String stock = restTemplate.getForObject(urlInventario, String.class);
            return stock;
        } catch (Exception e) {

            System.out.println("Inventario no disponible: " + e.getMessage());

            return "Stock no disponible";
        }
    }

    public Sucursal actualizarSucursal(Long id, Sucursal sucursal) {
        Sucursal buscado = sucursalRepository.findById(id).orElse(null);
        if (buscado == null)
            return null;

        buscado.setDireccionSucursal(sucursal.getDireccionSucursal());
        buscado.setHorario(sucursal.getHorario());
        buscado.setIdBodega(sucursal.getIdBodega());

        return sucursalRepository.save(buscado);
    }
}