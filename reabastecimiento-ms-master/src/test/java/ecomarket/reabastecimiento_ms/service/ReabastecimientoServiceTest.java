package ecomarket.reabastecimiento_ms.service;

import ecomarket.reabastecimiento_ms.model.ItemReabastecimiento;
import ecomarket.reabastecimiento_ms.model.ProductoDTO;
import ecomarket.reabastecimiento_ms.model.ProveedorDTO;
import ecomarket.reabastecimiento_ms.model.Reabastecimiento;
import ecomarket.reabastecimiento_ms.repository.ReabastecimientoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReabastecimientoServiceTest {

    @Mock
    private ReabastecimientoRepository reabastecimientoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReabastecimientoService reabastecimientoService;

    // Helper para crear un item de reabastecimiento
    private ItemReabastecimiento crearItem(Long idProducto, Integer cantidad) {
        ItemReabastecimiento item = new ItemReabastecimiento();
        item.setIdProducto(idProducto);
        item.setCantidad(cantidad);
        return item;
    }

    private ProveedorDTO crearProveedorDTO(Long id, String nombre, String rut) {
        ProveedorDTO proveedor = new ProveedorDTO();
        proveedor.setIdProveedor(id);
        proveedor.setNombre(nombre);
        proveedor.setRut(rut);
        return proveedor;
    }

    private ProductoDTO crearProductoDTO(Long id, String nombre) {
        ProductoDTO producto = new ProductoDTO();
        producto.setIdProducto(id);
        producto.setNombre(nombre);
        return producto;
    }

    @Test
    void testGuardarReabastecimientoConEstado() {
        Reabastecimiento reabastecimiento = new Reabastecimiento();
        reabastecimiento.setIdProveedor(1L);
        reabastecimiento.setEstado("APROBADO");
        reabastecimiento.setItems(new ArrayList<>(Arrays.asList(crearItem(10L, 5))));

        ProveedorDTO proveedor = crearProveedorDTO(1L, "Distribuidora Sur", "11111111-1");
        ProductoDTO producto = crearProductoDTO(10L, "Manzana");

        when(restTemplate.getForObject(anyString(), eq(ProveedorDTO.class))).thenReturn(proveedor);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(producto);
        when(reabastecimientoRepository.save(any(Reabastecimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        Reabastecimiento resultado = reabastecimientoService.guardarReabastecimiento(reabastecimiento);

        assertNotNull(resultado);
        assertEquals("APROBADO", resultado.getEstado()); // respeta el estado enviado
        assertNotNull(resultado.getFecha());
        assertEquals(1, resultado.getItems().size());

        verify(reabastecimientoRepository, times(1)).save(reabastecimiento);
    }

    @Test
    void testGuardarReabastecimientoEstadoPorDefecto() {
        Reabastecimiento reabastecimiento = new Reabastecimiento();
        reabastecimiento.setIdProveedor(1L);
        // estado en null -> debe quedar PENDIENTE
        reabastecimiento.setItems(new ArrayList<>(Arrays.asList(crearItem(10L, 5))));

        ProveedorDTO proveedor = crearProveedorDTO(1L, "Distribuidora Sur", "11111111-1");
        ProductoDTO producto = crearProductoDTO(10L, "Manzana");

        when(restTemplate.getForObject(anyString(), eq(ProveedorDTO.class))).thenReturn(proveedor);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(producto);
        when(reabastecimientoRepository.save(any(Reabastecimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        Reabastecimiento resultado = reabastecimientoService.guardarReabastecimiento(reabastecimiento);

        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());

        verify(reabastecimientoRepository, times(1)).save(reabastecimiento);
    }

    @Test
    void testGuardarReabastecimientoEstadoVacio() {
        Reabastecimiento reabastecimiento = new Reabastecimiento();
        reabastecimiento.setIdProveedor(1L);
        reabastecimiento.setEstado(""); // vacío -> debe quedar PENDIENTE
        reabastecimiento.setItems(new ArrayList<>(Arrays.asList(crearItem(10L, 5))));

        ProveedorDTO proveedor = crearProveedorDTO(1L, "Distribuidora Sur", "11111111-1");
        ProductoDTO producto = crearProductoDTO(10L, "Manzana");

        when(restTemplate.getForObject(anyString(), eq(ProveedorDTO.class))).thenReturn(proveedor);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(producto);
        when(reabastecimientoRepository.save(any(Reabastecimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        Reabastecimiento resultado = reabastecimientoService.guardarReabastecimiento(reabastecimiento);

        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());

        verify(reabastecimientoRepository, times(1)).save(reabastecimiento);
    }

    @Test
    void testGuardarReabastecimientoProveedorNoEncontrado() {
        Reabastecimiento reabastecimiento = new Reabastecimiento();
        reabastecimiento.setIdProveedor(999L);
        reabastecimiento.setEstado("PENDIENTE");
        reabastecimiento.setItems(new ArrayList<>(Arrays.asList(crearItem(10L, 5))));

        when(restTemplate.getForObject(anyString(), eq(ProveedorDTO.class))).thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reabastecimientoService.guardarReabastecimiento(reabastecimiento));

        assertEquals("Proveedor no encontrado", exception.getMessage());

        verify(reabastecimientoRepository, never()).save(any(Reabastecimiento.class));
    }

    @Test
    void testGuardarReabastecimientoProductoNoEncontrado() {
        Reabastecimiento reabastecimiento = new Reabastecimiento();
        reabastecimiento.setIdProveedor(1L);
        reabastecimiento.setEstado("PENDIENTE");
        reabastecimiento.setItems(new ArrayList<>(Arrays.asList(crearItem(10L, 5))));

        ProveedorDTO proveedor = crearProveedorDTO(1L, "Distribuidora Sur", "11111111-1");

        when(restTemplate.getForObject(anyString(), eq(ProveedorDTO.class))).thenReturn(proveedor);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reabastecimientoService.guardarReabastecimiento(reabastecimiento));

        assertEquals("Producto no encontrado: 10", exception.getMessage());

        verify(reabastecimientoRepository, never()).save(any(Reabastecimiento.class));
    }

    @Test
    void testListarReabastecimiento() {
        Reabastecimiento r1 = new Reabastecimiento();
        r1.setIdPedidoReabastecimiento(1L);
        Reabastecimiento r2 = new Reabastecimiento();
        r2.setIdPedidoReabastecimiento(2L);

        when(reabastecimientoRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<Reabastecimiento> resultado = reabastecimientoService.listarReabastecimiento();

        assertEquals(2, resultado.size());

        verify(reabastecimientoRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdExistente() {
        Reabastecimiento reabastecimiento = new Reabastecimiento();
        reabastecimiento.setIdPedidoReabastecimiento(1L);

        when(reabastecimientoRepository.findById(1L)).thenReturn(Optional.of(reabastecimiento));

        Optional<Reabastecimiento> resultado = reabastecimientoService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdPedidoReabastecimiento());

        verify(reabastecimientoRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNoExistente() {
        when(reabastecimientoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Reabastecimiento> resultado = reabastecimientoService.findById(99L);

        assertFalse(resultado.isPresent());

        verify(reabastecimientoRepository, times(1)).findById(99L);
    }
}