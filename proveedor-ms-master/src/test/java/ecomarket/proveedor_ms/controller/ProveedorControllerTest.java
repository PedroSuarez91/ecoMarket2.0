package ecomarket.proveedor_ms.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.proveedor_ms.model.Proveedor;
import ecomarket.proveedor_ms.service.ProveedorService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProveedorController.class)
@ActiveProfiles("test")
public class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockitoBean
    private ProveedorService proveedorService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetProveedores() throws Exception {
        Proveedor p1 = new Proveedor(1L, "Distribuidora Sur", "11111111-1");
        Proveedor p2 = new Proveedor(2L, "Comercial Norte", "22222222-2");

        Mockito.when(proveedorService.listar()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/v1/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Distribuidora Sur")))
                .andExpect(jsonPath("$[1].rut", is("22222222-2")));
    }

    @Test
    void testPostProveedor() throws Exception {
        Proveedor nuevo = new Proveedor(null, "Distribuidora Sur", "11111111-1");
        Proveedor guardado = new Proveedor(1L, "Distribuidora Sur", "11111111-1");

        Mockito.when(proveedorService.guardarProveedor(any(Proveedor.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/v1/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProveedor").value(1L))
                .andExpect(jsonPath("$.nombre").value("Distribuidora Sur"))
                .andExpect(jsonPath("$.rut").value("11111111-1"));
    }

    @Test
    void testGetProveedorExistente() throws Exception {
        Proveedor buscado = new Proveedor(2L, "Comercial Norte", "22222222-2");

        Mockito.when(proveedorService.findById(2L)).thenReturn(buscado);

        mockMvc.perform(get("/api/v1/proveedores/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProveedor").value(2L))
                .andExpect(jsonPath("$.nombre").value("Comercial Norte"));
    }

    @Test
    void testGetProveedorNoExistente() {
        Mockito.when(proveedorService.findById(99L))
                .thenThrow(new RuntimeException("Proveedor no encontrado: 99"));

        assertThrows(Exception.class,
                () -> mockMvc.perform(get("/api/v1/proveedores/99")));
    }

    @Test
    void testPutProveedor() throws Exception {
        Proveedor actualizado = new Proveedor(1L, "Distribuidora Centro", "33333333-3");

        Mockito.when(proveedorService.modificar(eq(1L), any(Proveedor.class)))
                .thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/proveedores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProveedor").value(1L))
                .andExpect(jsonPath("$.nombre").value("Distribuidora Centro"))
                .andExpect(jsonPath("$.rut").value("33333333-3"));
    }

    @Test
    void testPutProveedorInexistente() {
        Proveedor proveedor = new Proveedor(null, "Distribuidora Centro", "33333333-3");

        Mockito.when(proveedorService.modificar(eq(99L), any(Proveedor.class)))
                .thenThrow(new RuntimeException("Proveedor no encontrado: 99"));

        assertThrows(Exception.class,
                () -> mockMvc.perform(put("/api/v1/proveedores/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proveedor))));
    }

    @Test
    void testDeleteProveedor() throws Exception {
        Mockito.doNothing().when(proveedorService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/proveedores/1"))
                .andExpect(status().isOk());
    }
}
