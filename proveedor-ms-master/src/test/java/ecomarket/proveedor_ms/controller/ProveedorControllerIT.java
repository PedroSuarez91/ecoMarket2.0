package ecomarket.proveedor_ms.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.proveedor_ms.model.Proveedor;
import ecomarket.proveedor_ms.repository.ProveedorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProveedorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProveedorRepository proveedorRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void cleanDb() {
        proveedorRepository.deleteAll();
    }

    @Test
    void testCrearYObtenerProveedor() throws Exception {
        Proveedor proveedor = new Proveedor(null, "Distribuidora Sur", "11111111-1");

        mockMvc.perform(post("/api/v1/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proveedor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProveedor").exists())
                .andExpect(jsonPath("$.nombre").value("Distribuidora Sur"));

        mockMvc.perform(get("/api/v1/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Distribuidora Sur"))
                .andExpect(jsonPath("$[0].rut").value("11111111-1"));
    }

    @Test
    void testEliminarProveedor() throws Exception {
        Proveedor proveedor = new Proveedor(null, "Comercial Norte", "22222222-2");
        Proveedor guardado = proveedorRepository.save(proveedor);

        mockMvc.perform(delete("/api/v1/proveedores/" + guardado.getIdProveedor()))
                .andExpect(status().isOk());

        assertThrows(Exception.class,
                () -> mockMvc.perform(get("/api/v1/proveedores/" + guardado.getIdProveedor())));
    }

    @Test
    void testActualizarProveedor() throws Exception {
        Proveedor proveedor = new Proveedor(null, "Distribuidora Sur", "11111111-1");
        Proveedor guardado = proveedorRepository.save(proveedor);

        Proveedor actualizado = new Proveedor(null, "Distribuidora Centro", "33333333-3");

        mockMvc.perform(put("/api/v1/proveedores/" + guardado.getIdProveedor())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Distribuidora Centro"))
                .andExpect(jsonPath("$.rut").value("33333333-3"));
    }
}