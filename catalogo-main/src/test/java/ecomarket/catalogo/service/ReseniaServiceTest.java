package ecomarket.catalogo.service;

import ecomarket.catalogo.model.Producto;
import ecomarket.catalogo.model.Resenia;
import ecomarket.catalogo.repository.ProductoRepository;
import ecomarket.catalogo.repository.ReseniaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReseniaServiceTest {

    @Mock
    private ReseniaRepository reseniaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ReseniaService reseniaService;

    private Producto crearProducto(Long id, String nombre) {
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setNombre(nombre);
        return producto;
    }

    private Resenia crearResenia(Long id, String comentario, Integer calificacion, Producto producto) {
        Resenia resenia = new Resenia();
        resenia.setIdResenia(id);
        resenia.setComentario(comentario);
        resenia.setCalificacion(calificacion);
        resenia.setFechaResenia(LocalDate.now());
        resenia.setProducto(producto);
        return resenia;
    }

    @Test
    void testRegistrarReseniaExitosa() {
        Producto producto = crearProducto(10L, "Manzana");
        Resenia resenia = crearResenia(null, "Excelente", 5, producto);
        Resenia guardada = crearResenia(1L, "Excelente", 5, producto);

        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(reseniaRepository.save(resenia)).thenReturn(guardada);

        Resenia resultado = reseniaService.registrarResenia(resenia);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdResenia());
        assertEquals("Excelente", resultado.getComentario());

        verify(productoRepository, times(1)).findById(10L);
        verify(reseniaRepository, times(1)).save(resenia);
    }

    @Test
    void testRegistrarReseniaSinProducto() {
        Resenia resenia = crearResenia(null, "Excelente", 5, null);

        Resenia resultado = reseniaService.registrarResenia(resenia);

        assertNull(resultado);

        verify(productoRepository, never()).findById(any());
        verify(reseniaRepository, never()).save(any(Resenia.class));
    }

    @Test
    void testRegistrarReseniaProductoSinId() {
        Producto producto = crearProducto(null, "Manzana"); // sin id
        Resenia resenia = crearResenia(null, "Excelente", 5, producto);

        Resenia resultado = reseniaService.registrarResenia(resenia);

        assertNull(resultado);

        verify(productoRepository, never()).findById(any());
        verify(reseniaRepository, never()).save(any(Resenia.class));
    }

    @Test
    void testRegistrarReseniaProductoNoExiste() {
        Producto producto = crearProducto(99L, "Inexistente");
        Resenia resenia = crearResenia(null, "Excelente", 5, producto);

        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Resenia resultado = reseniaService.registrarResenia(resenia);

        assertNull(resultado);

        verify(productoRepository, times(1)).findById(99L);
        verify(reseniaRepository, never()).save(any(Resenia.class));
    }

    @Test
    void testListarResenias() {
        Producto producto = crearProducto(10L, "Manzana");
        Resenia r1 = crearResenia(1L, "Buena", 4, producto);
        Resenia r2 = crearResenia(2L, "Mala", 1, producto);

        when(reseniaRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<Resenia> resultado = reseniaService.listarResenias();

        assertEquals(2, resultado.size());

        verify(reseniaRepository, times(1)).findAll();
    }

    @Test
    void testListarPorProducto() {
        Producto producto = crearProducto(10L, "Manzana");
        Resenia r1 = crearResenia(1L, "Buena", 4, producto);

        when(reseniaRepository.findByProducto_IdProducto(10L)).thenReturn(Arrays.asList(r1));

        List<Resenia> resultado = reseniaService.listarPorProducto(10L);

        assertEquals(1, resultado.size());

        verify(reseniaRepository, times(1)).findByProducto_IdProducto(10L);
    }

    @Test
    void testFindByIdExistente() {
        Producto producto = crearProducto(10L, "Manzana");
        Resenia resenia = crearResenia(1L, "Buena", 4, producto);

        when(reseniaRepository.findById(1L)).thenReturn(Optional.of(resenia));

        Optional<Resenia> resultado = reseniaService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdResenia());

        verify(reseniaRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNoExistente() {
        when(reseniaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Resenia> resultado = reseniaService.findById(99L);

        assertFalse(resultado.isPresent());

        verify(reseniaRepository, times(1)).findById(99L);
    }

    @Test
    void testActualizarReseniaExistente() {
        Producto producto = crearProducto(10L, "Manzana");
        Resenia existente = crearResenia(1L, "Buena", 4, producto);
        Resenia datos = crearResenia(null, "Excelente", 5, producto);

        when(reseniaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(reseniaRepository.save(existente)).thenReturn(existente);

        Resenia resultado = reseniaService.actualizarResenia(1L, datos);

        assertNotNull(resultado);
        assertEquals("Excelente", resultado.getComentario());
        assertEquals(5, resultado.getCalificacion());

        verify(reseniaRepository, times(1)).findById(1L);
        verify(reseniaRepository, times(1)).save(existente);
    }

    @Test
    void testActualizarReseniaNoExistente() {
        Resenia datos = crearResenia(null, "Excelente", 5, null);

        when(reseniaRepository.findById(99L)).thenReturn(Optional.empty());

        Resenia resultado = reseniaService.actualizarResenia(99L, datos);

        assertNull(resultado);

        verify(reseniaRepository, times(1)).findById(99L);
        verify(reseniaRepository, never()).save(any(Resenia.class));
    }

    @Test
    void testEliminarResenia() {
        doNothing().when(reseniaRepository).deleteById(1L);

        reseniaService.eliminarResenia(1L);

        verify(reseniaRepository, times(1)).deleteById(1L);
    }
}

