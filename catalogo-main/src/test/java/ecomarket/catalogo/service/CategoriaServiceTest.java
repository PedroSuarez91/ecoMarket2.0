package ecomarket.catalogo.service;

import ecomarket.catalogo.model.Categoria;
import ecomarket.catalogo.repository.CategoriaRepository;

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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria crearCategoria(Long id, String nombre, String tipoProducto) {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(id);
        categoria.setNombreCategoria(nombre);
        categoria.setTipoProducto(tipoProducto);
        return categoria;
    }

    @Test
    void testCrearCategoria() {
        Categoria categoria = crearCategoria(null, "Frutas", "Alimento");
        Categoria guardada = crearCategoria(1L, "Frutas", "Alimento");

        when(categoriaRepository.save(categoria)).thenReturn(guardada);

        Categoria resultado = categoriaService.crearCategoria(categoria);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCategoria());
        assertEquals("Frutas", resultado.getNombreCategoria());
        assertEquals("Alimento", resultado.getTipoProducto());

        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void testListarCategorias() {
        Categoria c1 = crearCategoria(1L, "Frutas", "Alimento");
        Categoria c2 = crearCategoria(2L, "Limpieza", "Hogar");

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<Categoria> resultado = categoriaService.listarCategorias();

        assertEquals(2, resultado.size());
        assertEquals("Frutas", resultado.get(0).getNombreCategoria());
        assertEquals("Limpieza", resultado.get(1).getNombreCategoria());

        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdExistente() {
        Categoria categoria = crearCategoria(1L, "Frutas", "Alimento");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Optional<Categoria> resultado = categoriaService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdCategoria());

        verify(categoriaRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNoExistente() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Categoria> resultado = categoriaService.findById(99L);

        assertFalse(resultado.isPresent());

        verify(categoriaRepository, times(1)).findById(99L);
    }

    @Test
    void testEliminarCategoria() {
        doNothing().when(categoriaRepository).deleteById(1L);

        categoriaService.eliminarCategoria(1L);

        verify(categoriaRepository, times(1)).deleteById(1L);
    }
}
