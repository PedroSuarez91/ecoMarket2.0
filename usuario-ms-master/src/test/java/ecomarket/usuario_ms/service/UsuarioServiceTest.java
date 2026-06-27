package ecomarket.usuario_ms.service;

import ecomarket.usuario_ms.model.Usuario;
import ecomarket.usuario_ms.repository.UsuarioRepository;

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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testGuardarUsuario() {
        Usuario usuario = new Usuario(0L, "Juan", "Perez", "juan@mail.com", "1234", "ADMIN");
        Usuario guardado = new Usuario(1L, "Juan", "Perez", "juan@mail.com", "1234", "ADMIN");

        when(usuarioRepository.save(usuario)).thenReturn(guardado);

        Usuario resultado = usuarioService.guardarUsuario(usuario);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Perez", resultado.getApellido());
        assertEquals("juan@mail.com", resultado.getEmailUsuario());
        assertEquals("ADMIN", resultado.getRol());

        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testListarUsuarios() {
        Usuario u1 = new Usuario(1L, "Juan", "Perez", "juan@mail.com", "1234", "ADMIN");
        Usuario u2 = new Usuario(2L, "Ana", "Soto", "ana@mail.com", "5678", "CLIENTE");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("Ana", resultado.get(1).getNombre());

        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdExistente() {
        Usuario usuario = new Usuario(1L, "Juan", "Perez", "juan@mail.com", "1234", "ADMIN");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdUsuario());
        assertEquals("Juan", resultado.get().getNombre());

        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNoExistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.findById(99L);

        assertFalse(resultado.isPresent());

        verify(usuarioRepository, times(1)).findById(99L);
    }

    @Test
    void testFindByEmailUsuarioExistente() {
        Usuario usuario = new Usuario(1L, "Juan", "Perez", "juan@mail.com", "1234", "ADMIN");

        when(usuarioRepository.findByEmailUsuario("juan@mail.com")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.findByEmailUsuario("juan@mail.com");

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals("juan@mail.com", resultado.getEmailUsuario());
        assertEquals("Juan", resultado.getNombre());

        verify(usuarioRepository, times(1)).findByEmailUsuario("juan@mail.com");
    }

    @Test
    void testFindByEmailUsuarioNoExistente() {
        when(usuarioRepository.findByEmailUsuario("noexiste@mail.com")).thenReturn(Optional.empty());

        Usuario resultado = usuarioService.findByEmailUsuario("noexiste@mail.com");

        assertNull(resultado);

        verify(usuarioRepository, times(1)).findByEmailUsuario("noexiste@mail.com");
    }

    @Test
    void testActualizarUsuarioExistente() {
        Usuario existente = new Usuario(1L, "Juan", "Perez", "juan@mail.com", "1234", "ADMIN");
        Usuario datosNuevos = new Usuario(0L, "Pedro", "Gomez", "pedro@mail.com", "9999", "CLIENTE");
        Usuario actualizado = new Usuario(1L, "Pedro", "Gomez", "pedro@mail.com", "9999", "CLIENTE");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(existente)).thenReturn(actualizado);

        Usuario resultado = usuarioService.actualizarUsuario(1L, datosNuevos);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals("Pedro", resultado.getNombre());
        assertEquals("Gomez", resultado.getApellido());
        assertEquals("pedro@mail.com", resultado.getEmailUsuario());
        assertEquals("CLIENTE", resultado.getRol());

        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(existente);
    }

    @Test
    void testActualizarUsuarioNoExistente() {
        Usuario datosNuevos = new Usuario(0L, "Pedro", "Gomez", "pedro@mail.com", "9999", "CLIENTE");

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.actualizarUsuario(99L, datosNuevos));

        assertEquals("Usuario no encontrado: ", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(99L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testEliminarUsuario() {
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}
