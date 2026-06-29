package ecomarket.catalogo.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.catalogo.model.Producto;
import ecomarket.catalogo.model.Resenia;
import ecomarket.catalogo.service.ReseniaService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReseniaController.class)
@ActiveProfiles("test")
public class ReseniaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @SuppressWarnings("removal")
        @MockitoBean
        private ReseniaService reseniaService;

        private ObjectMapper objectMapper = new ObjectMapper();

        private Resenia crearResenia(Long id, String comentario, Integer calificacion) {
                Resenia resenia = new Resenia();
                resenia.setIdResenia(id);
                resenia.setComentario(comentario);
                resenia.setCalificacion(calificacion);
                resenia.setFechaResenia(LocalDate.now());
                return resenia;
        }

        private Resenia crearReseniaConProducto(Long id, String comentario, Integer calificacion, Long idProducto) {
                Resenia resenia = crearResenia(id, comentario, calificacion);
                Producto producto = new Producto();
                producto.setIdProducto(idProducto);
                resenia.setProducto(producto);
                return resenia;
        }

        @Test
        void testGetResenias() throws Exception {
                Mockito.when(reseniaService.listarResenias())
                                .thenReturn(Arrays.asList(crearResenia(1L, "Buena", 4), crearResenia(2L, "Mala", 1)));

                mockMvc.perform(get("/api/v1/resenias"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].comentario").value("Buena"));
        }

        @Test
        void testGetReseniasVacio() throws Exception {
                Mockito.when(reseniaService.listarResenias()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/resenias"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testGetPorProducto() throws Exception {
                Mockito.when(reseniaService.listarPorProducto(10L))
                                .thenReturn(Arrays.asList(crearResenia(1L, "Buena", 4)));

                mockMvc.perform(get("/api/v1/resenias/producto/10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void testGetPorProductoVacio() throws Exception {
                Mockito.when(reseniaService.listarPorProducto(99L)).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/resenias/producto/99"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testPostResenia() throws Exception {
                Resenia nueva = crearReseniaConProducto(null, "Excelente", 5, 10L);
                Resenia guardada = crearReseniaConProducto(1L, "Excelente", 5, 10L);

                Mockito.when(reseniaService.registrarResenia(any(Resenia.class))).thenReturn(guardada);

                mockMvc.perform(post("/api/v1/resenias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nueva)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.idResenia").value(1L))
                                .andExpect(jsonPath("$.comentario").value("Excelente"));
        }

        @Test
        void testPostReseniaNoEncontrada() throws Exception {
                Resenia nueva = crearReseniaConProducto(null, "Excelente", 5, 99L);

                Mockito.when(reseniaService.registrarResenia(any(Resenia.class))).thenReturn(null);

                mockMvc.perform(post("/api/v1/resenias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nueva)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testPutReseniaExistente() throws Exception {
                Resenia actualizada = crearResenia(1L, "Actualizada", 3);

                Mockito.when(reseniaService.actualizarResenia(eq(1L), any(Resenia.class))).thenReturn(actualizada);

                mockMvc.perform(put("/api/v1/resenias/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(actualizada)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.comentario").value("Actualizada"));
        }

        @Test
        void testPutReseniaNoExistente() throws Exception {
                Resenia datos = crearResenia(null, "Actualizada", 3);

                Mockito.when(reseniaService.actualizarResenia(eq(99L), any(Resenia.class))).thenReturn(null);

                mockMvc.perform(put("/api/v1/resenias/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(datos)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testDeleteResenia() throws Exception {
                Mockito.doNothing().when(reseniaService).eliminarResenia(1L);

                mockMvc.perform(delete("/api/v1/resenias/1"))
                                .andExpect(status().isNoContent());
        }
}
