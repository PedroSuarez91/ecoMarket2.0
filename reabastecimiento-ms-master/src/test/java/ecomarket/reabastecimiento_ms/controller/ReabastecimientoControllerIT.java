package ecomarket.reabastecimiento_ms.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.reabastecimiento_ms.model.ProductoDTO;
import ecomarket.reabastecimiento_ms.model.ProveedorDTO;
import ecomarket.reabastecimiento_ms.model.Reabastecimiento;
import ecomarket.reabastecimiento_ms.repository.ReabastecimientoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReabastecimientoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReabastecimientoRepository reabastecimientoRepository;

    @SuppressWarnings("removal")
    @MockitoBean
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void cleanDb() {
        reabastecimientoRepository.deleteAll();
    }

    @Test
    void testCrearYObtenerReabastecimiento() throws Exception {
        ProveedorDTO proveedor = new ProveedorDTO();
        proveedor.setIdProveedor(1L);
        proveedor.setNombre("Distribuidora Sur");
        proveedor.setRut("11111111-1");

        ProductoDTO producto = new ProductoDTO();
        producto.setIdProducto(10L);
        producto.setNombre("Manzana");

        when(restTemplate.getForObject(anyString(), eq(ProveedorDTO.class))).thenReturn(proveedor);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(producto);

        String body = "{ \"idProveedor\": 1, \"estado\": \"PENDIENTE\", \"items\": [ { \"idProducto\": 10, \"cantidad\": 5 } ] }";

        mockMvc.perform(post("/api/v1/reabastecimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPedidoReabastecimiento").exists())
                .andExpect(jsonPath("$.idProveedor").value(1L))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        mockMvc.perform(get("/api/v1/reabastecimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProveedor").value(1L));
    }

    @Test
    void testCrearReabastecimientoProveedorNoEncontrado() throws Exception {
        when(restTemplate.getForObject(anyString(), eq(ProveedorDTO.class))).thenReturn(null);

        String body = "{ \"idProveedor\": 999, \"estado\": \"PENDIENTE\", \"items\": [ { \"idProducto\": 10, \"cantidad\": 5 } ] }";

        mockMvc.perform(post("/api/v1/reabastecimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void testObtenerReabastecimientoNoExistente() throws Exception {
        mockMvc.perform(get("/api/v1/reabastecimientos/999"))
                .andExpect(status().isNoContent());
    }
}