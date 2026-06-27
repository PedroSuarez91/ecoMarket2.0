package ecomarket.carro_ms.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.carro_ms.model.Carro;
import ecomarket.carro_ms.service.CarroService;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarroController.class)
@ActiveProfiles("test")
public class CarroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockitoBean
    private CarroService carroService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetCarros() throws Exception {
        Carro c1 = new Carro();
        c1.setIdCarro(1L);
        Carro c2 = new Carro();
        c2.setIdCarro(2L);

        Mockito.when(carroService.listarCarro()).thenReturn(Arrays.asList(c1, c2));

        mockMvc.perform(get("/api/v1/carros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idCarro").value(1L))
                .andExpect(jsonPath("$[1].idCarro").value(2L));
    }

    @Test
    void testGetCarrosVacio() throws Exception {
        Mockito.when(carroService.listarCarro()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/carros"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testPostCarro() throws Exception {
        Carro nuevo = new Carro();
        nuevo.setIdUsuario(1L);

        Carro guardado = new Carro();
        guardado.setIdCarro(1L);
        guardado.setIdUsuario(1L);
        guardado.setNombreUsuario("Juan Perez");
        guardado.setTotal(1000.0);

        Mockito.when(carroService.guardarCarro(any(Carro.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/v1/carros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCarro").value(1L))
                .andExpect(jsonPath("$.nombreUsuario").value("Juan Perez"))
                .andExpect(jsonPath("$.total").value(1000.0));
    }

    @Test
    void testPostCarroConflicto() throws Exception {
        Carro nuevo = new Carro();
        nuevo.setIdUsuario(1L);

        Mockito.when(carroService.guardarCarro(any(Carro.class)))
                .thenThrow(new RuntimeException("Error al procesar carro"));

        mockMvc.perform(post("/api/v1/carros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetCarroExistente() throws Exception {
        Carro buscado = new Carro();
        buscado.setIdCarro(1L);
        buscado.setNombreUsuario("Juan Perez");

        Mockito.when(carroService.findById(1L)).thenReturn(Optional.of(buscado));

        mockMvc.perform(get("/api/v1/carros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCarro").value(1L))
                .andExpect(jsonPath("$.nombreUsuario").value("Juan Perez"));
    }

    @Test
    void testGetCarroNoExistente() throws Exception {
        Mockito.when(carroService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/carros/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarCarro() throws Exception {
        Mockito.doNothing().when(carroService).eliminarCarro(1L);

        mockMvc.perform(delete("/api/v1/carros/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarCarroError() throws Exception {
        Mockito.doThrow(new RuntimeException("Error al eliminar"))
                .when(carroService).eliminarCarro(99L);

        mockMvc.perform(delete("/api/v1/carros/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarCarroExistente() throws Exception {
        Carro actualizado = new Carro();
        actualizado.setIdCarro(1L);
        actualizado.setTipoEntrega("DESPACHO");
        actualizado.setIdDireccion(5L);

        Mockito.when(carroService.actualizar(eq(1L), any(Carro.class)))
                .thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/carros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCarro").value(1L))
                .andExpect(jsonPath("$.tipoEntrega").value("DESPACHO"))
                .andExpect(jsonPath("$.idDireccion").value(5L));
    }

    @Test
    void testActualizarCarroNoExistente() throws Exception {
        Carro carro = new Carro();
        carro.setTipoEntrega("DESPACHO");

        Mockito.when(carroService.actualizar(eq(99L), any(Carro.class)))
                .thenThrow(new RuntimeException("Carro no encontrado"));

        mockMvc.perform(put("/api/v1/carros/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carro)))
                .andExpect(status().isNotFound());
    }
}
