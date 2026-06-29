package ecomarket.pedido.controller;


import ecomarket.pedido.model.Direccion;
import ecomarket.pedido.service.DireccionService;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DireccionController.class)
@ActiveProfiles("test")
public class DireccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DireccionService direccionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Direccion dirEjemplo() {
        return new Direccion(1L, "Calle 1", "123", "RM", "Santiago", "Providencia", 8320000);
    }

    @Test
    void testGetDireccionesConContenido() throws Exception {
        Mockito.when(direccionService.listarDirecciones()).thenReturn(Arrays.asList(dirEjemplo()));

        mockMvc.perform(get("/api/v1/direcciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].calle").value("Calle 1"));
    }

    @Test
    void testGetDireccionesVacio204() throws Exception {
        Mockito.when(direccionService.listarDirecciones()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/direcciones"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetDireccionExistente() throws Exception {
        Mockito.when(direccionService.findById(1L)).thenReturn(Optional.of(dirEjemplo()));

        mockMvc.perform(get("/api/v1/direcciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comuna").value("Providencia"));
    }

    @Test
    void testGetDireccionInexistente204() throws Exception {
        Mockito.when(direccionService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/direcciones/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testPostDireccion201() throws Exception {
        Direccion nueva = new Direccion(null, "Calle 1", "123", "RM", "Santiago", "Providencia", 8320000);
        Mockito.when(direccionService.guardarDireccion(any(Direccion.class))).thenReturn(dirEjemplo());

        mockMvc.perform(post("/api/v1/direcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idDireccion").value(1L));
    }

    @Test
    void testPutDireccion200() throws Exception {
        Mockito.when(direccionService.actualizar(eq(1L), any(Direccion.class))).thenReturn(dirEjemplo());

        mockMvc.perform(put("/api/v1/direcciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dirEjemplo())))
                .andExpect(status().isOk());
    }

    @Test
    void testPutDireccion404() throws Exception {
        Mockito.when(direccionService.actualizar(eq(99L), any(Direccion.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/direcciones/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dirEjemplo())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteDireccion204() throws Exception {
        Mockito.doNothing().when(direccionService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/direcciones/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    void testPostDireccionFallaDevuelve409() throws Exception {
        Direccion nueva = new Direccion(null, "Calle 1", "123", "RM", "Santiago", "Providencia", 8320000);
        Mockito.when(direccionService.guardarDireccion(any(Direccion.class)))
                .thenThrow(new RuntimeException("error al guardar"));
        mockMvc.perform(post("/api/v1/direcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isConflict());
    }
}