package ecomarket.reabastecimiento_ms.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.reabastecimiento_ms.model.Reabastecimiento;
import ecomarket.reabastecimiento_ms.service.ReabastecimientoService;

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
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReabastecimientoController.class)
@ActiveProfiles("test")
public class ReabastecimientoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @SuppressWarnings("removal")
        @MockitoBean
        private ReabastecimientoService reabastecimientoService;

        private ObjectMapper objectMapper = new ObjectMapper();

        @Test
        void testGetReabastecimientos() throws Exception {
                Reabastecimiento r1 = new Reabastecimiento();
                r1.setIdPedidoReabastecimiento(1L);
                r1.setIdProveedor(1L);
                r1.setEstado("PENDIENTE");

                Reabastecimiento r2 = new Reabastecimiento();
                r2.setIdPedidoReabastecimiento(2L);
                r2.setIdProveedor(2L);
                r2.setEstado("APROBADO");

                Mockito.when(reabastecimientoService.listarReabastecimiento()).thenReturn(Arrays.asList(r1, r2));

                mockMvc.perform(get("/api/v1/reabastecimientos"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].idPedidoReabastecimiento").value(1L))
                                .andExpect(jsonPath("$[1].estado").value("APROBADO"));
        }

        @Test
        void testGetReabastecimientosVacio() throws Exception {
                Mockito.when(reabastecimientoService.listarReabastecimiento()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/reabastecimientos"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testPostReabastecimiento() throws Exception {
                Reabastecimiento nuevo = new Reabastecimiento();
                nuevo.setIdProveedor(1L);
                nuevo.setEstado("PENDIENTE");

                Reabastecimiento guardado = new Reabastecimiento();
                guardado.setIdPedidoReabastecimiento(1L);
                guardado.setIdProveedor(1L);
                guardado.setEstado("PENDIENTE");

                Mockito.when(reabastecimientoService.guardarReabastecimiento(any(Reabastecimiento.class)))
                                .thenReturn(guardado);

                mockMvc.perform(post("/api/v1/reabastecimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevo)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.idPedidoReabastecimiento").value(1L))
                                .andExpect(jsonPath("$.idProveedor").value(1L))
                                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
        }

        @Test
        void testPostReabastecimientoConflicto() throws Exception {
                Reabastecimiento nuevo = new Reabastecimiento();
                nuevo.setIdProveedor(999L);

                Mockito.when(reabastecimientoService.guardarReabastecimiento(any(Reabastecimiento.class)))
                                .thenThrow(new RuntimeException("Proveedor no encontrado"));

                mockMvc.perform(post("/api/v1/reabastecimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevo)))
                                .andExpect(status().isConflict());
        }

        @Test
        void testGetReabastecimientoExistente() throws Exception {
                Reabastecimiento buscado = new Reabastecimiento();
                buscado.setIdPedidoReabastecimiento(1L);
                buscado.setIdProveedor(1L);
                buscado.setEstado("PENDIENTE");

                Mockito.when(reabastecimientoService.findById(1L)).thenReturn(Optional.of(buscado));

                mockMvc.perform(get("/api/v1/reabastecimientos/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idPedidoReabastecimiento").value(1L))
                                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
        }

        @Test
        void testGetReabastecimientoNoExistente() throws Exception {
                Mockito.when(reabastecimientoService.findById(99L)).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/v1/reabastecimientos/99"))
                                .andExpect(status().isNoContent());
        }
}
