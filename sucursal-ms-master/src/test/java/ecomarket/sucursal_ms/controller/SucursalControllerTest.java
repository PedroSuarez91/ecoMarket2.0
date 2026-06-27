package ecomarket.sucursal_ms.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.sucursal_ms.model.Sucursal;
import ecomarket.sucursal_ms.model.SucursalDTO;
import ecomarket.sucursal_ms.service.SucursalService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

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

@WebMvcTest(SucursalController.class)
@ActiveProfiles("test")
public class SucursalControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @SuppressWarnings("removal")
        @MockitoBean
        private SucursalService sucursalService;

        private ObjectMapper objectMapper = new ObjectMapper();

        @Test
        void testCrearSucursal() throws Exception {
                Sucursal nueva = new Sucursal(null, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 1L);
                Sucursal guardada = new Sucursal(1L, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 1L);

                Mockito.when(sucursalService.crearSucursal(any(Sucursal.class))).thenReturn(guardada);

                mockMvc.perform(post("/api/v1/sucursales")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nueva)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idSucursal").value(1L))
                                .andExpect(jsonPath("$.direccionSucursal").value("Av. Siempre Viva 123"))
                                .andExpect(jsonPath("$.horario").value("09:00-18:00"));
        }

        @Test
        void testCrearSucursalConflicto() throws Exception {
                Sucursal nueva = new Sucursal(null, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 1L);

                Mockito.when(sucursalService.crearSucursal(any(Sucursal.class)))
                                .thenThrow(new RuntimeException("Error en BD"));

                mockMvc.perform(post("/api/v1/sucursales")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nueva)))
                                .andExpect(status().isConflict());
        }

        @Test
        void testListarSucursales() throws Exception {
                Sucursal s1 = new Sucursal(1L, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 1L);
                Sucursal s2 = new Sucursal(2L, "Calle Falsa 456", "10:00-19:00", "Con devoluciones", 2L);

                Mockito.when(sucursalService.listarSucursales()).thenReturn(Arrays.asList(s1, s2));

                mockMvc.perform(get("/api/v1/sucursales"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].direccionSucursal", is("Av. Siempre Viva 123")))
                                .andExpect(jsonPath("$[1].horario", is("10:00-19:00")));
        }

        @Test
        void testListarSucursalesVacio() throws Exception {
                Mockito.when(sucursalService.listarSucursales()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/sucursales"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testObtenerDetalleExistente() throws Exception {
                SucursalDTO dto = new SucursalDTO();
                dto.setIdSucursal(1L);
                dto.setDireccionSucursal("Av. Siempre Viva 123");
                dto.setHorario("09:00-18:00");
                dto.setNombreBodega("Bodega Central");
                dto.setCapacidadMax(1000);

                Mockito.when(sucursalService.obtenerSucursalDTO(1L)).thenReturn(dto);

                mockMvc.perform(get("/api/v1/sucursales/1/detalle"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idSucursal").value(1L))
                                .andExpect(jsonPath("$.nombreBodega").value("Bodega Central"))
                                .andExpect(jsonPath("$.capacidadMax").value(1000));
        }

        @Test
        void testObtenerDetalleNoExistente() throws Exception {
                Mockito.when(sucursalService.obtenerSucursalDTO(99L)).thenReturn(null);

                mockMvc.perform(get("/api/v1/sucursales/99/detalle"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testConsultarStockExistente() throws Exception {
                Mockito.when(sucursalService.consultarStock(1L)).thenReturn("Stock: 250 unidades");

                mockMvc.perform(get("/api/v1/sucursales/1/stock"))
                                .andExpect(status().isOk());
        }

        @Test
        void testConsultarStockNoExistente() throws Exception {
                Mockito.when(sucursalService.consultarStock(99L)).thenReturn(null);

                mockMvc.perform(get("/api/v1/sucursales/99/stock"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testObtenerSucursalExistente() throws Exception {
                Sucursal buscada = new Sucursal(1L, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 1L);

                Mockito.when(sucursalService.findById(1L)).thenReturn(Optional.of(buscada));

                mockMvc.perform(get("/api/v1/sucursales/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idSucursal").value(1L))
                                .andExpect(jsonPath("$.direccionSucursal").value("Av. Siempre Viva 123"));
        }

        @Test
        void testObtenerSucursalNoExistente() throws Exception {
                Mockito.when(sucursalService.findById(99L)).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/v1/sucursales/99"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testEliminarSucursalExistente() throws Exception {
                Mockito.when(sucursalService.eliminarSucursal(1L)).thenReturn(true);

                mockMvc.perform(delete("/api/v1/sucursales/1"))
                                .andExpect(status().isOk());
        }

        @Test
        void testEliminarSucursalNoExistente() throws Exception {
                Mockito.when(sucursalService.eliminarSucursal(99L)).thenReturn(false);

                mockMvc.perform(delete("/api/v1/sucursales/99"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testActualizarSucursalExistente() throws Exception {
                Sucursal actualizada = new Sucursal(1L, "Calle Nueva 789", "08:00-17:00", "Sin devoluciones", 2L);

                Mockito.when(sucursalService.actualizarSucursal(eq(1L), any(Sucursal.class)))
                                .thenReturn(actualizada);

                mockMvc.perform(put("/api/v1/sucursales/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(actualizada)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idSucursal").value(1L))
                                .andExpect(jsonPath("$.direccionSucursal").value("Calle Nueva 789"))
                                .andExpect(jsonPath("$.horario").value("08:00-17:00"));
        }

        @Test
        void testActualizarSucursalNoExistente() throws Exception {
                Sucursal sucursal = new Sucursal(null, "Calle Nueva 789", "08:00-17:00", "Sin devoluciones", 2L);

                Mockito.when(sucursalService.actualizarSucursal(eq(99L), any(Sucursal.class)))
                                .thenReturn(null);

                mockMvc.perform(put("/api/v1/sucursales/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sucursal)))
                                .andExpect(status().isNotFound());
        }
}
