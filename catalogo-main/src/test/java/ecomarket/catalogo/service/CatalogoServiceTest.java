package ecomarket.catalogo.service;

import ecomarket.catalogo.model.Catalogo;
import ecomarket.catalogo.repository.CatalogoRepository;

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
class CatalogoServiceTest {

    @Mock
    private CatalogoRepository catalogoRepository;

    @InjectMocks
    private CatalogoService catalogoService;

    private Catalogo crearCatalogo(Long id, String nombre, LocalDate fecha) {
        Catalogo catalogo = new Catalogo();
        catalogo.setIdCatalogo(id);
        catalogo.setNombreCatalogo(nombre);
        catalogo.setFechaActualizacion(fecha);
        return catalogo;
    }

    @Test
    void testCrearCatalogo() {
        Catalogo catalogo = crearCatalogo(null, "Catalogo Verano", LocalDate.now());
        Catalogo guardado = crearCatalogo(1L, "Catalogo Verano", LocalDate.now());

        when(catalogoRepository.save(catalogo)).thenReturn(guardado);

        Catalogo resultado = catalogoService.crearCatalogo(catalogo);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCatalogo());
        assertEquals("Catalogo Verano", resultado.getNombreCatalogo());

        verify(catalogoRepository, times(1)).save(catalogo);
    }

    @Test
    void testListarCatalogo() {
        Catalogo c1 = crearCatalogo(1L, "Catalogo Verano", LocalDate.now());
        Catalogo c2 = crearCatalogo(2L, "Catalogo Invierno", LocalDate.now());

        when(catalogoRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<Catalogo> resultado = catalogoService.listarCatalogo();

        assertEquals(2, resultado.size());
        assertEquals("Catalogo Verano", resultado.get(0).getNombreCatalogo());
        assertEquals("Catalogo Invierno", resultado.get(1).getNombreCatalogo());

        verify(catalogoRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdExistente() {
        Catalogo catalogo = crearCatalogo(1L, "Catalogo Verano", LocalDate.now());

        when(catalogoRepository.findById(1L)).thenReturn(Optional.of(catalogo));

        Optional<Catalogo> resultado = catalogoService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdCatalogo());

        verify(catalogoRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNoExistente() {
        when(catalogoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Catalogo> resultado = catalogoService.findById(99L);

        assertFalse(resultado.isPresent());

        verify(catalogoRepository, times(1)).findById(99L);
    }

    @Test
    void testActualizarCatalogoExistente() {
        Catalogo existente = crearCatalogo(1L, "Catalogo Verano", LocalDate.of(2025, 1, 1));
        Catalogo datos = crearCatalogo(null, "Catalogo Otono", LocalDate.of(2025, 6, 1));

        when(catalogoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(catalogoRepository.save(existente)).thenReturn(existente);

        Catalogo resultado = catalogoService.actualizarCatalogo(1L, datos);

        assertNotNull(resultado);
        assertEquals("Catalogo Otono", resultado.getNombreCatalogo());
        assertEquals(LocalDate.of(2025, 6, 1), resultado.getFechaActualizacion());

        verify(catalogoRepository, times(1)).findById(1L);
        verify(catalogoRepository, times(1)).save(existente);
    }

    @Test
    void testActualizarCatalogoNoExistente() {
        Catalogo datos = crearCatalogo(null, "Catalogo Otono", LocalDate.now());

        when(catalogoRepository.findById(99L)).thenReturn(Optional.empty());

        Catalogo resultado = catalogoService.actualizarCatalogo(99L, datos);

        assertNull(resultado);

        verify(catalogoRepository, times(1)).findById(99L);
        verify(catalogoRepository, never()).save(any(Catalogo.class));
    }

    @Test
    void testEliminarCatalogo() {
        doNothing().when(catalogoRepository).deleteById(1L);

        catalogoService.eliminarCatalogo(1L);

        verify(catalogoRepository, times(1)).deleteById(1L);
    }
}
