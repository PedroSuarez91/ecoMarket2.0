package com.example.inventario_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.inventario_service.model.Inventario;
import com.example.inventario_service.model.ProductoDTO;
import com.example.inventario_service.repository.InventarioRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class InventarioService {
    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Inventario crearInventario(Inventario inventario){
        try{
            String url = "http://localhost:8090/api/v1/productos/" + inventario.getIdProducto();
            ProductoDTO producto = restTemplate.getForObject(url, ProductoDTO.class);
            
            if (producto != null) {
                Optional<Inventario> inventarioExistente = inventarioRepository.findByIdProducto(inventario.getIdProducto());

                if (inventarioExistente.isPresent()) {
                    Inventario inv = inventarioExistente.get();
                    inv.setStockDisponible(inv.getStockDisponible() + inventario.getStockDisponible());
                    inv.setFechaActualizacion(LocalDate.now());
                    return inventarioRepository.save(inv);
                }
                inventario.setNombre(producto.getNombre());
                inventario.setFechaActualizacion(LocalDate.now());

                return inventarioRepository.save(inventario);
            }
            return null;
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Producto no encontrado en Catalogo");
        } catch (Exception e) {
            System.out.println("*************************");
            System.out.println("Catalogo no disponible: " + e.getMessage());
            System.out.println("*************************");
            throw new RuntimeException("Servicio de Catalogo no disponible, intente mas tarde");
        }
    }

    public List<Inventario> obtenerInventarios() {
        return inventarioRepository.findAll();
    }

    public Integer getStockPorBodega(Long idBodega) {
        List<Inventario> inventarios = inventarioRepository.findByIdBodega(idBodega);
        return inventarios.stream()
                .mapToInt(Inventario::getStockDisponible)
                .sum();
    }

    public Inventario actualizarInventario(Long id, Inventario inventario) {
        Inventario buscado = inventarioRepository.findById(id).orElse(null);
        if (buscado == null) return null;

        buscado.setIdProducto(inventario.getIdProducto());
        buscado.setIdBodega(inventario.getIdBodega());
        buscado.setNombre(inventario.getNombre());
        buscado.setStockDisponible(inventario.getStockDisponible());
        buscado.setStockMinimo(inventario.getStockMinimo());

        return inventarioRepository.save(buscado);
    }

    public Inventario descontarStock(Long idProducto, Integer cantidad) {
        Inventario buscado = inventarioRepository.findByIdProducto(idProducto).orElse(null);
        if (buscado == null) return null;

        if (buscado.getStockDisponible() < cantidad) return null; // no hay suficiente stock

        buscado.setStockDisponible(buscado.getStockDisponible() - cantidad);
        buscado.setFechaActualizacion(LocalDate.now());
        return inventarioRepository.save(buscado);
    }

    public List<Inventario> alertasStockMinimo() {
        return inventarioRepository.findProductosBajoStockMinimo();
    }
}