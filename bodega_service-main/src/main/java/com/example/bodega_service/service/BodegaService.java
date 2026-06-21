package com.example.bodega_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.bodega_service.model.Bodega;
import com.example.bodega_service.model.BodegaDTO;
import com.example.bodega_service.model.SucursalDTO;
import com.example.bodega_service.repository.BodegaRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class BodegaService {
    @Autowired
    private BodegaRepository bodegaRepository;
    @Autowired
    private RestTemplate restTemplate;

    public Bodega crearBodega(Bodega bodega) {
        System.out.println("*************************");
        System.out.println(bodega);
        System.out.println("*************************");
        return bodegaRepository.save(bodega);
    }

    public List<Bodega> listarBodegas() {
        return bodegaRepository.findAll();
    }

    public Optional<Bodega> findById(Long id) {
        return bodegaRepository.findById(id);
    }

    public boolean eliminarBodega(Long id) {
        if (bodegaRepository.existsById(id)) {
            bodegaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public BodegaDTO obtenerBodegaDTO(Long idBodega) {
        Bodega bodega = bodegaRepository.findById(idBodega).orElse(null);
        if (bodega == null) return null;

        // 2. Arma el DTO con los datos propios de Bodega
        BodegaDTO dto = new BodegaDTO();
        dto.setIdBodega(bodega.getIdBodega());
        dto.setNombreBodega(bodega.getNombreBodega());
        dto.setCapacidadMax(bodega.getCapacidadMax());
        dto.setActiva(bodega.isActiva());

    // Conexion a Sucursal
        try {
            String urlSucursal = "http://localhost:8084/api/v1/sucursales/" + bodega.getIdSucursal();
            SucursalDTO sucursal = restTemplate.getForObject(urlSucursal, SucursalDTO.class);
            if (sucursal != null) {
                dto.setDireccionSucursal(sucursal.getDireccionSucursal());
                System.out.println("*************************");
                System.out.println("Sucursal encontrada: " + sucursal);
                System.out.println("*************************");
            }
        } catch (Exception e) {
            System.out.println("*************************");
            System.out.println("Sucursal no disponible: " + e.getMessage());
            System.out.println("*************************");
        }
    // Conexion a Inventario
            try {
                String urlInventario = "http://localhost:9093/api/v1/inventario/stockPorBodega/"+bodega.getIdBodega();
                String totalStock = restTemplate.getForObject(urlInventario, String.class);
                if (totalStock != null) {
                    Integer total = Integer.parseInt(totalStock.replace("Total de productos: ", ""));
                    dto.setCapacidadOcupada(total);
                }
            } catch (Exception e) {
                System.out.println("Inventario no disponible: " + e.getMessage());
            }

        return dto;
    }

    public List<BodegaDTO> listarBodegasDTO() {
        List<Bodega> bodegas = bodegaRepository.findAll();
        List<BodegaDTO> lista = new ArrayList<>();

        for (Bodega bodega : bodegas) {
            BodegaDTO dto = new BodegaDTO();
            dto.setIdBodega(bodega.getIdBodega());
            dto.setNombreBodega(bodega.getNombreBodega());
            dto.setCapacidadMax(bodega.getCapacidadMax());
            dto.setActiva(bodega.isActiva());

            try {
                String urlSucursal = "http://localhost:8084/api/v1/sucursales/" + bodega.getIdSucursal();
                SucursalDTO sucursal = restTemplate.getForObject(urlSucursal, SucursalDTO.class);
                if (sucursal != null) {
                    dto.setDireccionSucursal(sucursal.getDireccionSucursal());
                }
            } catch (Exception e) {
                System.out.println("Sucursal no disponible: " + e.getMessage());
            }

            try {
                String urlInventario = "http://localhost:9093/api/v1/inventario/stockPorBodega/" + bodega.getIdBodega();
                String totalStock = restTemplate.getForObject(urlInventario, String.class);
                if (totalStock != null) {
                    Integer total = Integer.parseInt(totalStock.replace("Total de productos: ", ""));
                    dto.setCapacidadOcupada(total);
                }
            } catch (Exception e) {
                System.out.println("Inventario no disponible: " + e.getMessage());
            }

            lista.add(dto);
        }
        return lista;
    }
}