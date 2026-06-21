package com.example.inventario_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.inventario_service.model.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    
    Optional<Inventario> findByIdProducto(Long idProducto);
    List<Inventario> findByIdBodega(Long idBodega);

    @Query("SELECT i FROM Inventario i WHERE i.stockDisponible <= i.stockMinimo")
    List<Inventario> findProductosBajoStockMinimo();
}