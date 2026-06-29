package ecomarket.cupon.controller;


import ecomarket.cupon.model.Cupon;
import ecomarket.cupon.service.CuponService;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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

@WebMvcTest(CuponController.class)
@ActiveProfiles("test")
public class CuponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CuponService cuponService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListarConContenido() throws Exception {
        Cupon c1 = new Cupon(1L, "VERANO10", 10.0, true, null);
        Cupon c2 = new Cupon(2L, "INVIERNO20", 20.0, true, null);

        Mockito.when(cuponService.listar()).thenReturn(Arrays.asList(c1, c2));

        mockMvc.perform(get("/api/v1/cupones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigo", is("VERANO10")))
                .andExpect(jsonPath("$[1].porcentajeDescuento", is(20.0)));
    }

    @Test
    void testListarVacioDevuelve204() throws Exception {
        Mockito.when(cuponService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/cupones"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGuardarDevuelve201() throws Exception {
        Cupon nuevo = new Cupon(null, "VERANO10", 10.0, true, LocalDate.of(2026, 12, 31));
        Cupon guardado = new Cupon(1L, "VERANO10", 10.0, true, LocalDate.of(2026, 12, 31));

        Mockito.when(cuponService.guardar(any(Cupon.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/v1/cupones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCupon").value(1L))
                .andExpect(jsonPath("$.codigo").value("VERANO10"));
    }

    @Test
    void testObtenerPorIdExistente() throws Exception {
        Cupon c = new Cupon(1L, "VERANO10", 10.0, true, null);
        Mockito.when(cuponService.findById(1L)).thenReturn(Optional.of(c));

        mockMvc.perform(get("/api/v1/cupones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("VERANO10"));
    }

    @Test
    void testObtenerPorIdInexistenteDevuelve204() throws Exception {
        Mockito.when(cuponService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/cupones/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testBuscarPorCodigoExistente() throws Exception {
        Cupon c = new Cupon(1L, "VERANO10", 10.0, true, null);
        Mockito.when(cuponService.buscarPorCodigo("VERANO10")).thenReturn(Optional.of(c));

        mockMvc.perform(get("/api/v1/cupones/codigo/VERANO10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCupon").value(1L));
    }

    @Test
    void testBuscarPorCodigoInexistenteDevuelve404() throws Exception {
        Mockito.when(cuponService.buscarPorCodigo("NADA")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/cupones/codigo/NADA"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testValidar() throws Exception {
        Mockito.when(cuponService.validar("VERANO10")).thenReturn(true);

        mockMvc.perform(get("/api/v1/cupones/validar/VERANO10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testActualizarExistente() throws Exception {
        Cupon actualizado = new Cupon(1L, "INVIERNO20", 20.0, false, null);

        Mockito.when(cuponService.actualizar(eq(1L), any(Cupon.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/cupones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("INVIERNO20"))
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void testActualizarInexistenteDevuelve404() throws Exception {
        Cupon datos = new Cupon(null, "X", 5.0, true, null);

        Mockito.when(cuponService.actualizar(eq(99L), any(Cupon.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/cupones/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarDevuelve204() throws Exception {
        Mockito.doNothing().when(cuponService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/cupones/1"))
                .andExpect(status().isNoContent());
    }

    @Test
void testGuardarCodigoDuplicadoDevuelve409() throws Exception {
    Cupon nuevo = new Cupon(null, "VERANO10", 10.0, true, null);
    Mockito.when(cuponService.guardar(any(Cupon.class)))
            .thenThrow(new RuntimeException("codigo duplicado"));
    mockMvc.perform(post("/api/v1/cupones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(nuevo)))
            .andExpect(status().isConflict());   // 409
}
}