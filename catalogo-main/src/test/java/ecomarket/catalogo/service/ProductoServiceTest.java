package ecomarket.catalogo.service;

import ecomarket.catalogo.model.Producto;
import ecomarket.catalogo.repository.ProductoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto crearProducto(Long id, String nombre, String marca, Integer precio) {
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setNombre(nombre);
        producto.setMarca(marca);
        producto.setPrecioUnitario(precio);
        producto.setEstado(true);
        return producto;
    }

    @Test
    void testRegistrarProducto() {
        Producto producto = crearProducto(null, "Manzana", "FrutCorp", 500);
        Producto guardado = crearProducto(1L, "Manzana", "FrutCorp", 500);

        when(productoRepository.save(producto)).thenReturn(guardado);

        Producto resultado = productoService.registrarProducto(producto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProducto());
        assertEquals("Manzana", resultado.getNombre());

        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void testListarProductos() {
        Producto p1 = crearProducto(1L, "Manzana", "FrutCorp", 500);
        Producto p2 = crearProducto(2L, "Pera", "FrutCorp", 600);

        when(productoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Producto> resultado = productoService.listarProductos();

        assertEquals(2, resultado.size());

        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdProductoExistente() {
        Producto producto = crearProducto(1L, "Manzana", "FrutCorp", 500);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Optional<Producto> resultado = productoService.findByIdProducto(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdProducto());

        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdProductoNoExistente() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Producto> resultado = productoService.findByIdProducto(99L);

        assertFalse(resultado.isPresent());

        verify(productoRepository, times(1)).findById(99L);
    }

    @Test
    void testFindByCategoria() {
        Producto p1 = crearProducto(1L, "Manzana", "FrutCorp", 500);

        when(productoRepository.findByCategorias_IdCategoria(5L)).thenReturn(Arrays.asList(p1));

        List<Producto> resultado = productoService.findByCategoria(5L);

        assertEquals(1, resultado.size());
        assertEquals("Manzana", resultado.get(0).getNombre());

        verify(productoRepository, times(1)).findByCategorias_IdCategoria(5L);
    }

    @Test
    void testActualizarProductoExistente() {
        Producto existente = crearProducto(1L, "Manzana", "FrutCorp", 500);
        Producto datos = crearProducto(null, "Manzana Verde", "FrutCorp Premium", 700);
        datos.setCategorias(new ArrayList<>());

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(existente)).thenReturn(existente);

        Producto resultado = productoService.actualizarProducto(1L, datos);

        assertNotNull(resultado);
        assertEquals("Manzana Verde", resultado.getNombre());
        assertEquals("FrutCorp Premium", resultado.getMarca());
        assertEquals(700, resultado.getPrecioUnitario());

        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(existente);
    }

    @Test
    void testActualizarProductoNoExistente() {
        Producto datos = crearProducto(null, "Manzana Verde", "FrutCorp", 700);

        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Producto resultado = productoService.actualizarProducto(99L, datos);

        assertNull(resultado);

        verify(productoRepository, times(1)).findById(99L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testFindByRangoPrecio() {
        Producto p1 = crearProducto(1L, "Manzana", "FrutCorp", 500);

        when(productoRepository.findByPrecioUnitarioBetween(400, 600)).thenReturn(Arrays.asList(p1));

        List<Producto> resultado = productoService.findByRangoPrecio(400, 600);

        assertEquals(1, resultado.size());

        verify(productoRepository, times(1)).findByPrecioUnitarioBetween(400, 600);
    }

    @Test
    void testFindByPrecioMaximo() {
        Producto p1 = crearProducto(1L, "Manzana", "FrutCorp", 500);

        when(productoRepository.findByPrecioUnitarioLessThanEqual(600)).thenReturn(Arrays.asList(p1));

        List<Producto> resultado = productoService.findByPrecioMaximo(600);

        assertEquals(1, resultado.size());

        verify(productoRepository, times(1)).findByPrecioUnitarioLessThanEqual(600);
    }

    @Test
    void testFindByPrecioMinimo() {
        Producto p1 = crearProducto(1L, "Manzana", "FrutCorp", 500);

        when(productoRepository.findByPrecioUnitarioGreaterThanEqual(400)).thenReturn(Arrays.asList(p1));

        List<Producto> resultado = productoService.findByPrecioMinimo(400);

        assertEquals(1, resultado.size());

        verify(productoRepository, times(1)).findByPrecioUnitarioGreaterThanEqual(400);
    }

    @Test
    void testBuscarPorNombre() {
        Producto p1 = crearProducto(1L, "Manzana", "FrutCorp", 500);

        when(productoRepository.findByNombreContainingIgnoreCase("manz")).thenReturn(Arrays.asList(p1));

        List<Producto> resultado = productoService.buscarPorNombre("manz");

        assertEquals(1, resultado.size());
        assertEquals("Manzana", resultado.get(0).getNombre());

        verify(productoRepository, times(1)).findByNombreContainingIgnoreCase("manz");
    }

    @Test
    void testFindByMarca() {
        Producto p1 = crearProducto(1L, "Manzana", "FrutCorp", 500);

        when(productoRepository.findByMarca("FrutCorp")).thenReturn(Arrays.asList(p1));

        List<Producto> resultado = productoService.findByMarca("FrutCorp");

        assertEquals(1, resultado.size());

        verify(productoRepository, times(1)).findByMarca("FrutCorp");
    }

    @Test
    void testEliminarProductoExistente() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        boolean resultado = productoService.eliminarProducto(1L);

        assertTrue(resultado);

        verify(productoRepository, times(1)).existsById(1L);
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarProductoNoExistente() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        boolean resultado = productoService.eliminarProducto(99L);

        assertFalse(resultado);

        verify(productoRepository, times(1)).existsById(99L);
        verify(productoRepository, never()).deleteById(99L);
    }
}
