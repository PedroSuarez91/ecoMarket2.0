package ecomarket.proveedor_ms.service;

import ecomarket.proveedor_ms.model.Proveedor;
import ecomarket.proveedor_ms.repository.ProveedorRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    @Test
    void testGuardarProveedor() {
        Proveedor proveedor = new Proveedor(null, "Distribuidora Sur", "11111111-1");
        Proveedor guardado = new Proveedor(1L, "Distribuidora Sur", "11111111-1");

        when(proveedorRepository.save(proveedor)).thenReturn(guardado);

        Proveedor resultado = proveedorService.guardarProveedor(proveedor);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProveedor());
        assertEquals("Distribuidora Sur", resultado.getNombre());
        assertEquals("11111111-1", resultado.getRut());

        verify(proveedorRepository, times(1)).save(proveedor);
    }

    @Test
    void testListar() {
        Proveedor p1 = new Proveedor(1L, "Distribuidora Sur", "11111111-1");
        Proveedor p2 = new Proveedor(2L, "Comercial Norte", "22222222-2");

        when(proveedorRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Proveedor> resultado = proveedorService.listar();

        assertEquals(2, resultado.size());
        assertEquals("Distribuidora Sur", resultado.get(0).getNombre());
        assertEquals("Comercial Norte", resultado.get(1).getNombre());

        verify(proveedorRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdExistente() {
        Proveedor proveedor = new Proveedor(1L, "Distribuidora Sur", "11111111-1");

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));

        Proveedor resultado = proveedorService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProveedor());
        assertEquals("Distribuidora Sur", resultado.getNombre());

        verify(proveedorRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNoExistente() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> proveedorService.findById(99L));

        assertEquals("Proveedor no encontrado: 99", exception.getMessage());

        verify(proveedorRepository, times(1)).findById(99L);
    }

    @Test
    void testModificarExistente() {
        Proveedor existente = new Proveedor(1L, "Distribuidora Sur", "11111111-1");
        Proveedor datosNuevos = new Proveedor(null, "Distribuidora Centro", "33333333-3");
        Proveedor actualizado = new Proveedor(1L, "Distribuidora Centro", "33333333-3");

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(proveedorRepository.save(existente)).thenReturn(actualizado);

        Proveedor resultado = proveedorService.modificar(1L, datosNuevos);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProveedor());
        assertEquals("Distribuidora Centro", resultado.getNombre());
        assertEquals("33333333-3", resultado.getRut());

        verify(proveedorRepository, times(1)).findById(1L);
        verify(proveedorRepository, times(1)).save(existente);
    }

    @Test
    void testModificarNoExistente() {
        Proveedor datosNuevos = new Proveedor(null, "Distribuidora Centro", "33333333-3");

        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> proveedorService.modificar(99L, datosNuevos));

        assertEquals("Proveedor no encontrado: 99", exception.getMessage());

        verify(proveedorRepository, times(1)).findById(99L);
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void testEliminar() {
        doNothing().when(proveedorRepository).deleteById(1L);

        proveedorService.eliminar(1L);

        verify(proveedorRepository, times(1)).deleteById(1L);
    }
}
