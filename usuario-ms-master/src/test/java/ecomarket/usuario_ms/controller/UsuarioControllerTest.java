package ecomarket.usuario_ms.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.usuario_ms.model.Usuario;
import ecomarket.usuario_ms.service.UsuarioService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
//DESCONTINUADO -- import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//DESCONTINUADO --- import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import java.util.Collections;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@ActiveProfiles("test")
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockitoBean
    private UsuarioService usuarioService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testListarUsuarios() throws Exception {
        Usuario u1 = new Usuario(1L, "Juan", "Perez", "juan@mail.com", "1234", "ADMIN");
        Usuario u2 = new Usuario(2L, "Ana", "Soto", "ana@mail.com", "5678", "CLIENTE");

        Mockito.when(usuarioService.listarUsuarios()).thenReturn(Arrays.asList(u1, u2));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Juan")))
                .andExpect(jsonPath("$[1].rol", is("CLIENTE")));
    }

    @Test
    void testGuardarUsuario() throws Exception {
        Usuario nuevo = new Usuario(null, "Juan", "Perez", "juan@mail.com", "1234", "ADMIN");
        Usuario guardado = new Usuario(1L, "Juan", "Perez", "juan@mail.com", "1234", "ADMIN");

        Mockito.when(usuarioService.guardarUsuario(any(Usuario.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"))
                .andExpect(jsonPath("$.emailUsuario").value("juan@mail.com"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    void testFindByIdExistente() throws Exception {
        Usuario buscado = new Usuario(2L, "Ana", "Soto", "ana@mail.com", "5678", "CLIENTE");

        Mockito.when(usuarioService.findById(2L)).thenReturn(Optional.of(buscado));

        mockMvc.perform(get("/api/v1/usuarios/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(2L))
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void testFindByIdNoExistente() throws Exception {
        Mockito.when(usuarioService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindByEmailExistente() throws Exception {
        Usuario buscado = new Usuario(1L, "Juan", "Perez", "juan@mail.com", "1234", "ADMIN");

        Mockito.when(usuarioService.findByEmailUsuario("juan@mail.com")).thenReturn(buscado);

        mockMvc.perform(get("/api/v1/usuarios/emailUsuario/juan@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.emailUsuario").value("juan@mail.com"));
    }

    @Test
    void testFindByEmailNoExistente() throws Exception {
        Mockito.when(usuarioService.findByEmailUsuario("noexiste@mail.com")).thenReturn(null);

        mockMvc.perform(get("/api/v1/usuarios/emailUsuario/noexiste@mail.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarUsuario() throws Exception {
        Usuario actualizado = new Usuario(1L, "Pedro", "Gomez", "pedro@mail.com", "9999", "CLIENTE");

        Mockito.when(usuarioService.actualizarUsuario(eq(1L), any(Usuario.class)))
                .thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.nombre").value("Pedro"))
                .andExpect(jsonPath("$.apellido").value("Gomez"))
                .andExpect(jsonPath("$.emailUsuario").value("pedro@mail.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void testActualizarUsuarioInexistente() throws Exception {
        Usuario usuario = new Usuario(null, "Pedro", "Gomez", "pedro@mail.com", "9999", "CLIENTE");

        Mockito.when(usuarioService.actualizarUsuario(eq(99L), any(Usuario.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado: "));

        mockMvc.perform(put("/api/v1/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarUsuario() throws Exception {
        Mockito.doNothing().when(usuarioService).eliminarUsuario(1L);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testListarUsuariosVacio() throws Exception {
        Mockito.when(usuarioService.listarUsuarios()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isNoContent());
    }
}
