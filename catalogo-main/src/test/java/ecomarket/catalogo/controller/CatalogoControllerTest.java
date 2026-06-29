package ecomarket.catalogo.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.catalogo.model.Catalogo;
import ecomarket.catalogo.service.CatalogoService;

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

@WebMvcTest(CatalogoController.class)
@ActiveProfiles("test")
public class CatalogoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @SuppressWarnings("removal")
        @MockitoBean
        private CatalogoService catalogoService;

        private ObjectMapper objectMapper = new ObjectMapper();

        private Catalogo crearCatalogo(Long id, String nombre) {
                Catalogo catalogo = new Catalogo();
                catalogo.setIdCatalogo(id);
                catalogo.setNombreCatalogo(nombre);
                catalogo.setFechaActualizacion(LocalDate.now());
                return catalogo;
        }

        @Test
        void testGetCatalogos() throws Exception {
                Mockito.when(catalogoService.listarCatalogo())
                                .thenReturn(Arrays.asList(crearCatalogo(1L, "Verano"), crearCatalogo(2L, "Invierno")));

                mockMvc.perform(get("/api/v1/catalogos"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].nombreCatalogo").value("Verano"));
        }

        @Test
        void testGetCatalogosVacio() throws Exception {
                Mockito.when(catalogoService.listarCatalogo()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/catalogos"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testGetCatalogoExistente() throws Exception {
                Mockito.when(catalogoService.findById(1L)).thenReturn(Optional.of(crearCatalogo(1L, "Verano")));

                mockMvc.perform(get("/api/v1/catalogos/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idCatalogo").value(1L))
                                .andExpect(jsonPath("$.nombreCatalogo").value("Verano"));
        }

        @Test
        void testGetCatalogoNoExistente() throws Exception {
                Mockito.when(catalogoService.findById(99L)).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/v1/catalogos/99"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testPostCatalogo() throws Exception {
                Catalogo nuevo = crearCatalogo(null, "Verano");
                Catalogo guardado = crearCatalogo(1L, "Verano");

                Mockito.when(catalogoService.crearCatalogo(any(Catalogo.class))).thenReturn(guardado);

                mockMvc.perform(post("/api/v1/catalogos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevo)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.idCatalogo").value(1L))
                                .andExpect(jsonPath("$.nombreCatalogo").value("Verano"));
        }

        @Test
        void testPutCatalogoExistente() throws Exception {
                Catalogo actualizado = crearCatalogo(1L, "Otono");

                Mockito.when(catalogoService.actualizarCatalogo(eq(1L), any(Catalogo.class))).thenReturn(actualizado);

                mockMvc.perform(put("/api/v1/catalogos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(actualizado)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombreCatalogo").value("Otono"));
        }

        @Test
        void testPutCatalogoNoExistente() throws Exception {
                Catalogo datos = crearCatalogo(null, "Otono");

                Mockito.when(catalogoService.actualizarCatalogo(eq(99L), any(Catalogo.class))).thenReturn(null);

                mockMvc.perform(put("/api/v1/catalogos/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(datos)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testDeleteCatalogo() throws Exception {
                Mockito.doNothing().when(catalogoService).eliminarCatalogo(1L);

                mockMvc.perform(delete("/api/v1/catalogos/1"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testPostCatalogoConflicto() throws Exception {
                Catalogo nuevo = crearCatalogo(null, "Verano");

                Mockito.when(catalogoService.crearCatalogo(any(Catalogo.class)))
                                .thenThrow(new RuntimeException("Error al crear catalogo"));

                mockMvc.perform(post("/api/v1/catalogos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevo)))
                                .andExpect(status().isConflict());
        }
}
