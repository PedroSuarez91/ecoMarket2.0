package ecomarket.catalogo.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.catalogo.model.Categoria;
import ecomarket.catalogo.service.CategoriaService;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriaController.class)
@ActiveProfiles("test")
public class CategoriaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @SuppressWarnings("removal")
        @MockitoBean
        private CategoriaService categoriaService;

        private ObjectMapper objectMapper = new ObjectMapper();

        private Categoria crearCategoria(Long id, String nombre, String tipo) {
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(id);
                categoria.setNombreCategoria(nombre);
                categoria.setTipoProducto(tipo);
                return categoria;
        }

        @Test
        void testGetCategorias() throws Exception {
                Mockito.when(categoriaService.listarCategorias())
                                .thenReturn(Arrays.asList(crearCategoria(1L, "Frutas", "Alimento"),
                                                crearCategoria(2L, "Limpieza", "Hogar")));

                mockMvc.perform(get("/api/v1/categorias"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].nombreCategoria").value("Frutas"));
        }

        @Test
        void testGetCategoriasVacio() throws Exception {
                Mockito.when(categoriaService.listarCategorias()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/categorias"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testGetCategoriaExistente() throws Exception {
                Mockito.when(categoriaService.findById(1L))
                                .thenReturn(Optional.of(crearCategoria(1L, "Frutas", "Alimento")));

                mockMvc.perform(get("/api/v1/categorias/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idCategoria").value(1L))
                                .andExpect(jsonPath("$.nombreCategoria").value("Frutas"));
        }

        @Test
        void testGetCategoriaNoExistente() throws Exception {
                Mockito.when(categoriaService.findById(99L)).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/v1/categorias/99"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testPostCategoria() throws Exception {
                Categoria nueva = crearCategoria(null, "Frutas", "Alimento");
                Categoria guardada = crearCategoria(1L, "Frutas", "Alimento");

                Mockito.when(categoriaService.crearCategoria(any(Categoria.class))).thenReturn(guardada);

                mockMvc.perform(post("/api/v1/categorias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nueva)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.idCategoria").value(1L))
                                .andExpect(jsonPath("$.nombreCategoria").value("Frutas"));
        }

        @Test
        void testDeleteCategoria() throws Exception {
                Mockito.doNothing().when(categoriaService).eliminarCategoria(1L);

                mockMvc.perform(delete("/api/v1/categorias/1"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testPostCategoriaConflicto() throws Exception {
                Categoria nueva = crearCategoria(null, "Frutas", "Alimento");

                Mockito.when(categoriaService.crearCategoria(any(Categoria.class)))
                                .thenThrow(new RuntimeException("Error al crear categoria"));

                mockMvc.perform(post("/api/v1/categorias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nueva)))
                                .andExpect(status().isConflict());
        }

}
